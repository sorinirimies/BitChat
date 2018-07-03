package ro.cluj.sorin.bitchat.ui.nearby

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.Payload
import ro.cluj.sorin.bitchat.model.Endpoint

interface NearbyConnectionsListener {
  /**
   * Called when advertising successfully starts. Override this method to act on the event.
   */
  fun onAdvertisingStarted()

  /**
   * Called when advertising fails to start. Override this method to act on the event.
   */
  fun onAdvertisingFailed()

  /**
   * Called when discovery successfully starts. Override this method to act on the event.
   */
  fun onDiscoveryStarted()

  /**
   * Called when discovery fails to start. Override this method to act on the event.
   */
  fun onDiscoveryFailed()

  /**
   * Called when a remote endpoint is discovered. To connect to the device, call [ ][.connectToEndpoint].
   */
  fun onEndpointDiscovered(endpoint: Endpoint)

  /**
   * Called when a connection with this endpoint has failed. Override this method to act on the
   * event.
   */
  fun onConnectionFailed(endpoint: Endpoint)

  /**
   * Called when a pending connection with a remote endpoint is created. Use [ConnectionInfo]
   * for metadata about the connection (like incoming vs outgoing, or the authentication token). If
   * we want to continue with the connection, call [.acceptConnection]. Otherwise,
   * call [.rejectConnection].
   */
  fun onConnectionInitiated(endpoint: Endpoint, connectionInfo: ConnectionInfo)

  /**
   * Called when someone has connected to us. Override this method to act on the event.
   */
  fun onEndpointConnected(endpoint: Endpoint)

  /**
   * Called when someone has disconnected. Override this method to act on the event.
   */
  fun onEndpointDisconnected(endpoint: Endpoint)

  /**
   * Someone connected to us has sent us data. Override this method to act on the event.
   *
   * @param endpoint The sender.
   * @param payload  The data.
   */
  fun onReceive(endpoint: Endpoint, payload: Payload)
}