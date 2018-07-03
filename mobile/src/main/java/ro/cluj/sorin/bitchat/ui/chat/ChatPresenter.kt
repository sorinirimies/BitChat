package ro.cluj.sorin.bitchat.ui.chat

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.Strategy
import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter
import ro.cluj.sorin.bitchat.model.ChatMessage
import ro.cluj.sorin.bitchat.model.Endpoint
import ro.cluj.sorin.bitchat.model.State
import ro.cluj.sorin.bitchat.ui.nearby.ConnectionsManager
import ro.cluj.sorin.bitchat.ui.nearby.NearbyConnectionsListener
import ro.cluj.sorin.bitchat.utils.MoshiAdapters
import timber.log.Timber

/**
 * Created by sorin on 12.05.18.
 */

class ChatPresenter(private val connectionsManager: ConnectionsManager?) : BasePresenter<ChatView>(),
  NearbyConnectionsListener {

  private var state = State.UNKNOWN

  init {
    connectionsManager?.setNearbyConnectionsListener(this)
  }

  override fun onConnectionInitiated(endpoint: Endpoint, connectionInfo: ConnectionInfo) {
    connectionsManager?.acceptConnection(endpoint)
  }

  override fun onAdvertisingStarted() {

  }

  override fun onAdvertisingFailed() {
  }

  override fun onDiscoveryStarted() {
  }

  override fun onDiscoveryFailed() {
  }

  override fun onEndpointDiscovered(endpoint: Endpoint) {
    connectionsManager?.stopDiscovering()
    connectionsManager?.connectToEndpoint(endpoint)
  }

  override fun onConnectionFailed(endpoint: Endpoint) {
    // Let's try someone else.
    if (state == State.SEARCHING) {
      connectionsManager?.startDiscovering(Strategy.P2P_STAR)
    }
  }

  override fun onEndpointConnected(endpoint: Endpoint) {
    setState(State.CONNECTED)
    view.showNearbyChatConnected()
  }

  override fun onEndpointDisconnected(endpoint: Endpoint) {
    setState(State.SEARCHING)
    view.showNearbyChatDisconnected()
  }

  override fun onReceive(endpoint: Endpoint, payload: Payload) {
    payload.asBytes()?.let { String(it) }?.let {
      val msg = MoshiAdapters.ChatMessage.fromJson(it)
      msg?.isSending = false
      if (msg != null) {
        view.showAddedMessage(msg)
      }
    }
  }

  fun sendMsg(msg: ChatMessage) {
    val msgJson = MoshiAdapters.ChatMessage.toJson(msg)
    connectionsManager?.send(Payload.fromBytes(msgJson.toByteArray()))
  }

  fun addMessage(msg: ChatMessage) {
    view.showAddedMessage(msg)
  }

  fun stopNearbyChat() {
    setState(State.UNKNOWN)
  }

  /**
   * The state has changed. I wonder what we'll be doing now.
   *
   * @param state The new state.
   */
  fun setState(state: State) {
    if (this.state == state) {
      Timber.w("State set to $state but already in that state")
      return
    }
    Timber.d("State set to $state")
    this.state = state
    onStateChanged(state)
  }

  private fun onStateChanged(newState: State) {
    when (newState) {
      State.CONNECTED -> {
        connectionsManager?.stopDiscovering()
        connectionsManager?.stopAdvertising()
      }
      State.SEARCHING -> {
        connectionsManager?.disconnectFromAllEndpoints()
        connectionsManager?.startDiscovering(Strategy.P2P_STAR)
        connectionsManager?.startAdvertising(Strategy.P2P_STAR)
      }
      State.UNKNOWN -> {
        connectionsManager?.stopAllEndpoints()
      }
    }
  }

  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }

}