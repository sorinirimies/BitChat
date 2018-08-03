package ro.cluj.sorin.bitchat.ui.map

import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter
import ro.cluj.sorin.bitchat.mqtt.BitChatMqttManager
import java.util.UUID

/**
 * Created by sorin on 12.05.18.
 */
private const val BROKER_URL = "tcp://totemz.ddns.net:4000"
private const val TOPIC_LOCATION = "/location"

class GroupsMapPresenter : BasePresenter<GroupsMapView>() {

  private var mqttManager = BitChatMqttManager({ payload -> }, { isConnected -> })

  fun mapIsReady(googleMap: GoogleMap) {
    view.showMap(googleMap)
  }

  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
    connectMqttClient(user.uid)
  }

  private fun connectMqttClient(uid: String) = mqttManager.connect(BROKER_URL,
      arrayOf(TOPIC_LOCATION),
      intArrayOf(1),
      UUID.randomUUID().toString(), uid)

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
    mqttManager.disconnect()
  }
}