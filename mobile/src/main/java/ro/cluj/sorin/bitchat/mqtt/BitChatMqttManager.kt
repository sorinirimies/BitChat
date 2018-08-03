package ro.cluj.sorin.bitchat.mqtt

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

/**
 * Created by sorin on 12.05.18.
 */

typealias MqttPayload = (Pair<String, MqttMessage>) -> Unit

typealias MqttConnectionStatus = (Boolean) -> Unit

class BitChatMqttManager(mqttPayload: MqttPayload, mqttConnectionStatus: MqttConnectionStatus) : MqttManager {
  private var maxNumberOfRetries = 4
  private var retryInterval = 4000L
  private var topics: Array<String> = arrayOf()
  private var qos: IntArray = intArrayOf()
  private var timerReconnect: Timer? = null
  private var retryCount = 0
  private var isMqttClientConnected = false
  private var mqttClient: MqttAsyncClient? = null
  private var user: String? = null
  private var clientId: String? = null
  private var explicitDisconnection = false

  private val channelMqttPayload: SendChannel<Pair<String, MqttMessage>> = actor(UI) {
    channel.consumeEach {
      mqttPayload.invoke(it)
    }
  }
  private val channelMqttConnectionState: SendChannel<Boolean> = actor(UI) {
    channel.consumeEach {
      mqttConnectionStatus.invoke(it)
    }
  }

  override fun connect(serverURI: String, topics: Array<String>, qos: IntArray, clientId: String?, user: String?) {
    if (isMqttClientConnected) {
      Timber.w("connect was called although the mqttClient is already connected")
      return
    }
    this@BitChatMqttManager.topics = topics
    this@BitChatMqttManager.qos = qos
    this@BitChatMqttManager.clientId = clientId
    this@BitChatMqttManager.user = user
    mqttClient = MqttAsyncClient(serverURI, clientId, MemoryPersistence())
    mqttClient?.setCallback(object : MqttCallback {
      @Throws(Exception::class)
      override fun messageArrived(topic: String, message: MqttMessage) {
        val msg = message.payload.toString(Charsets.UTF_8)
        Timber.tag(BitChatMqttManager::class.java.simpleName).w("Mqtt payload arrived: $msg")
        sendMqttPayload(topic to message)
      }

      override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit

      override fun connectionLost(cause: Throwable) {
        isMqttClientConnected = false
        sendMqttConnectionStatus(false)
        channelMqttConnectionState.close()
        Timber.w(cause, "MQTT connection lost")
        if (!explicitDisconnection) {
          resetTimer()
          retry()
        }
      }
    })
    val connectAction: IMqttActionListener = object : IMqttActionListener {
      override fun onSuccess(asyncActionToken: IMqttToken?) {
        isMqttClientConnected = true
        sendMqttConnectionStatus(true)
        Timber.w("MQTT connected")
        mqttClient?.subscribe(topics, qos, null, object : IMqttActionListener {
          override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.w("MQTT subscription successful")
          }

          override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
            Timber.e(exception, "MQTT could not subscribe")
          }
        })
      }

      override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
        isMqttClientConnected = false
        channelMqttPayload.close()
        channelMqttConnectionState.close()
        Timber.e(exception, "MQTT could not establish connection")
        if (!explicitDisconnection) {
          retry()
        }
      }
    }
    try {
      val options = MqttConnectOptions().apply {
        isCleanSession = true
        userName = user
      }
      mqttClient?.connect(options, null, connectAction)
    } catch (cause: MqttException) {
      Timber.e(cause, "MQTT connecting issue: ")
    }
  }

  fun sendMqttConnectionStatus(isConnected: Boolean = false) = launch { channelMqttConnectionState.send(isConnected) }

  fun sendMqttPayload(message: Pair<String, MqttMessage>) = launch { channelMqttPayload.send(message) }

  override fun disconnect() {
    if (!isMqttClientConnected) {
      Timber.w("disconnect was called although the mqttClient is not connected")
      return
    }
    val disconnectAction: IMqttActionListener = object : IMqttActionListener {
      override fun onSuccess(asyncActionToken: IMqttToken?) {
        Timber.w("Mqtt Client disconnected")
        isMqttClientConnected = false
        explicitDisconnection = true
        channelMqttPayload.close()
        channelMqttConnectionState.close()
      }

      override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
        Timber.e(exception, "Could not disconnect MQTT client")
      }
    }
    try {
      if (mqttClient?.isConnected == true)
        mqttClient?.disconnect(null, disconnectAction)
    } catch (cause: MqttException) {
      if (cause.reasonCode == MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED.toInt()) {
        isMqttClientConnected = false
        explicitDisconnection = true
        Timber.e(cause, "Client is already disconnected!")
      } else {
        Timber.e(cause, "Disconnection error")
      }
    }
  }

  override fun setRetryIntervalTime(retryInterval: Long) {
    this.retryInterval = retryInterval
  }

  override fun setMaxNumberOfRetires(maxNumberOfRetries: Int) {
    this.maxNumberOfRetries = maxNumberOfRetries
  }

  private fun resetTimer() {
    retryCount = 0
    timerReconnect?.let {
      it.cancel()
      it.purge()
    }
    timerReconnect = null
  }

  private fun retry() {
    if (timerReconnect == null) {
      timerReconnect = fixedRateTimer("mqtt_reconnect_timer", true, 0, retryInterval) {
        retryCount++
        Timber.w("MQTT reconnect retry $retryCount")
        if (mqttClient?.isConnected == true || retryCount > maxNumberOfRetries) {
          sendMqttConnectionStatus(isMqttClientConnected)
          cancel()
        }
        val loggedIn = user != null
        if (loggedIn) connect(mqttClient?.serverURI ?: "", topics, qos, clientId, user)
        else disconnect()
      }
    }
  }
}