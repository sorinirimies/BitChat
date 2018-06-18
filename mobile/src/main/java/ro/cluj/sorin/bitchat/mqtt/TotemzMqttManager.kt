package ro.cluj.sorin.bitchat.mqtt

import android.app.Application
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
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

class TotemzMqttManager(private val application: Application, private val accountManager: FirebaseAuth) : MqttManager {
  private var maxNumberOfRetries = 4
  private var retryInterval = 4000L
  private var topics: Array<String> = arrayOf()
  private var qos: IntArray = intArrayOf()
  private var timerReconnect: Timer? = null
  private var retryCount = 0
  private var isMqttClientConnected = false
  private var mqttClient: MqttAsyncClient? = null
  private val mqttMessageChannel by lazy { ConflatedBroadcastChannel<Pair<String, MqttMessage>>() }
  private val mqttConnectionStateChannel by lazy { ConflatedBroadcastChannel<Boolean>() }
  private var explicitDisconnection = false
  @SuppressWarnings("HardwareIds")
  override fun connect(serverURI: String, topics: Array<String>, qos: IntArray) {
    if (isMqttClientConnected) {
      Timber.w("connect was called although the mqttClient is already connected")
      return
    }
    this@TotemzMqttManager.topics = topics
    this@TotemzMqttManager.qos = qos
    val clientId = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
    mqttClient = MqttAsyncClient(serverURI, clientId, MemoryPersistence())
    mqttClient?.setCallback(object : MqttCallback {
      @Throws(Exception::class)
      override fun messageArrived(topic: String, message: MqttMessage) {
        val msg = message.payload.toString(Charsets.UTF_8)
        Timber.w("Message arrived: $msg")
        launch { mqttMessageChannel.send(topic to message) }
      }

      override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit

      override fun connectionLost(cause: Throwable) {
        isMqttClientConnected = false
        launch { mqttConnectionStateChannel.send(false) }
        mqttConnectionStateChannel.close()
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
        sendConnectionStatus(true)
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
        mqttMessageChannel.close()
        mqttConnectionStateChannel.close()
        Timber.e(exception, "MQTT could not establish connection")
        if (!explicitDisconnection) {
          retry()
        }
      }
    }
    try {
      val options = MqttConnectOptions().apply {
        isCleanSession = true
        userName = accountManager.currentUser?.displayName
      }
      mqttClient?.connect(options, null, connectAction)
    } catch (cause: MqttException) {
      Timber.e(cause, "MQTT connecting issue: ")
    }
  }

  fun sendConnectionStatus(isConnected: Boolean) = launch { mqttConnectionStateChannel.send(isConnected) }

  fun sendMqttMessage(message: Pair<String, MqttMessage>) = launch { mqttMessageChannel.send(message) }

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
        mqttMessageChannel.close()
        mqttConnectionStateChannel.close()
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
          sendConnectionStatus(isMqttClientConnected)
          cancel()
        }
        val loggedIn = accountManager.currentUser != null
        if (loggedIn) connect(mqttClient?.serverURI ?: "", topics, qos)
        else disconnect()
      }
    }
  }
}