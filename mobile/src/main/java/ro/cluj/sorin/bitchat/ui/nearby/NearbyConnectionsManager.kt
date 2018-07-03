package ro.cluj.sorin.bitchat.ui.nearby

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import ro.cluj.sorin.bitchat.model.Endpoint
import timber.log.Timber
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.Random

const val NEARBY_SERVICE_ID = "ro.cluj.sorin.bitchat.NEARBY_SERVICE_ID"
const val PREF_IS_NEARBY_ENABLED = "ro.cluj.sorin.bitchat.PREF_IS_NEARBY_ENABLED"

class ConnectionsManager(val context: Context) {

  private var nearbyConnectionsListener: NearbyConnectionsListener? = null

  fun setNearbyConnectionsListener(nearbyConnectionsListener: NearbyConnectionsListener) {
    this.nearbyConnectionsListener = nearbyConnectionsListener
  }

  /**
   * The devices we've discovered near us.
   */
  private val mDiscoveredEndpoints = HashMap<String, Endpoint>()

  /**
   * The devices we have pending connections to. They will stay pending until we call [ ][.acceptConnection] or [.rejectConnection].
   */
  private val pendingConnections = HashMap<String, Endpoint>()

  /**
   * The devices we are currently connected to. For advertisers, this may be large. For discoverers,
   * there will only be one entry in this map.
   */
  private val establishedConnections = HashMap<String, Endpoint>()

  /**
   * True if we are asking a discovered device to connect to us. While we ask, we cannot ask another
   * device.
   */
  private var isConnecting = false

  /**
   * True if we are discovering.
   */
  private var mIsDiscovering = false

  /**
   * True if we are advertising.
   */
  private var isAdvertising = false

  private val connectionsClient by lazy { Nearby.getConnectionsClient(context) }

  /**
   * Returns the client's name. Visible to others when connecting.
   */
  private val getName by lazy { generateRandomName() }

  /**
   * Sets the device to advertising mode. It will broadcast to other devices in discovery mode.
   * Either [.onAdvertisingStarted] or [.onAdvertisingFailed] will be called once
   * we've found out if we successfully entered this mode.
   */
  fun startAdvertising(strategy: Strategy) {
    isAdvertising = true
    val localEndpointName = getName
    connectionsClient
        .startAdvertising(
            localEndpointName,
            NEARBY_SERVICE_ID,
            mConnectionLifecycleCallback,
            AdvertisingOptions(strategy))
        .addOnSuccessListener {
          Timber.i("Now advertising endpoint $localEndpointName")
          nearbyConnectionsListener?.onAdvertisingStarted()
        }
        .addOnFailureListener {
          isAdvertising = false
          Timber.i("startAdvertising() failed.$it")
          nearbyConnectionsListener?.onAdvertisingFailed()
        }
  }

  /**
   * Stops advertising.
   */
  fun stopAdvertising() {
    isAdvertising = false
    connectionsClient.stopAdvertising()
  }

  /**
   * Returns `true` if currently advertising.
   */
  fun isAdvertising(): Boolean {
    return isAdvertising
  }

  /**
   * Accepts a connection request.
   */
  fun acceptConnection(endpoint: Endpoint) {
    connectionsClient
        .acceptConnection(endpoint.id, payloadCallback)
        .addOnFailureListener {
          Timber.tag("acceptConnection failed").w(it)
        }
  }

  /**
   * Rejects a connection request.
   */
  fun rejectConnection(endpoint: Endpoint) {
    connectionsClient
        .rejectConnection(endpoint.id)
        .addOnFailureListener {
          Timber.w("rejectConnection() failed.$it")
        }
  }

  /**
   * Sets the device to discovery mode. It will now listen for devices in advertising mode. Either
   * [.onDiscoveryStarted] or [.onDiscoveryFailed] will be called once we've found
   * out if we successfully entered this mode.
   */
  fun startDiscovering(strategy: Strategy) {
    mIsDiscovering = true
    mDiscoveredEndpoints.clear()
    connectionsClient
        .startDiscovery(
            NEARBY_SERVICE_ID, object : EndpointDiscoveryCallback() {
          override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Timber.d(String.format(
                "onEndpointFound(endpointId=%s, serviceId=%s, endpointName=%s)",
                endpointId, info.serviceId, info.endpointName))
            if (NEARBY_SERVICE_ID == info.serviceId) {
              val endpoint = Endpoint(endpointId, info.endpointName)
              mDiscoveredEndpoints[endpointId] = endpoint
              nearbyConnectionsListener?.onEndpointDiscovered(endpoint)
            }
          }

          override fun onEndpointLost(endpointId: String) {
            Timber.d(String.format("onEndpointLost(endpointId=%s)", endpointId))
          }
        }, DiscoveryOptions(strategy))
        .addOnSuccessListener { nearbyConnectionsListener?.onDiscoveryStarted() }
        .addOnFailureListener { e ->
          mIsDiscovering = false
          Timber.d("startDiscovering() failed.$e")
          nearbyConnectionsListener?.onDiscoveryFailed()
        }
  }

  /**
   * Stops discovery.
   */
  fun stopDiscovering() {
    mIsDiscovering = false
    connectionsClient.stopDiscovery()
  }

  /**
   * Returns `true` if currently discovering.
   */
  fun isDiscovering(): Boolean {
    return mIsDiscovering
  }

  /**
   * Disconnects from the given endpoint.
   */
  fun disconnect(endpoint: Endpoint) {
    connectionsClient.disconnectFromEndpoint(endpoint.id)
    establishedConnections.remove(endpoint.id)
  }

  /**
   * Disconnects from all currently connected endpoints.
   */
  fun disconnectFromAllEndpoints() {
    for (endpoint in establishedConnections.values) {
      connectionsClient.disconnectFromEndpoint(endpoint.id)
    }
    establishedConnections.clear()
  }

  /**
   * Resets and clears all state in Nearby Connections.
   */
  fun stopAllEndpoints() {
    connectionsClient.stopAllEndpoints()
    isAdvertising = false
    mIsDiscovering = false
    isConnecting = false
    mDiscoveredEndpoints.clear()
    pendingConnections.clear()
    establishedConnections.clear()
  }

  /**
   * Callbacks for connections to other devices.
   */
  private val mConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
      Timber.d(
          String.format(
              "onConnectionInitiated(endpointId=%s, endpointName=%s)",
              endpointId, connectionInfo.endpointName))
      val endpoint = Endpoint(endpointId, connectionInfo.endpointName)
      pendingConnections[endpointId] = endpoint
      nearbyConnectionsListener?.onConnectionInitiated(endpoint, connectionInfo)
    }

    override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
      Timber.d(String.format("onConnectionResponse(endpointId=%s, result=%s)", endpointId, result))

      // We're no longer connecting
      isConnecting = false

      if (!result.status.isSuccess) {
        Timber.w(
            "Connection failed. Received status: ${result.status}")
        pendingConnections.remove(endpointId)?.let { nearbyConnectionsListener?.onConnectionFailed(it) }
        return
      }
      pendingConnections.remove(endpointId)?.let { connectedToEndpoint(it) }
    }

    override fun onDisconnected(endpointId: String) {
      if (!establishedConnections.containsKey(endpointId)) {
        Timber.w("Unexpected disconnection from endpoint $endpointId")
        return
      }
      establishedConnections[endpointId]?.let { disconnectedFromEndpoint(it) }
    }
  }

  /**
   * Callbacks for payloads (bytes of data) sent from another device to us.
   */
  private val payloadCallback = object : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
      Timber.d(String.format("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payload))
      establishedConnections[endpointId]?.let { nearbyConnectionsListener?.onReceive(it, payload) }
    }

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
      Timber.d(String.format(
          "onPayloadTransferUpdate(endpointId=%s, update=%s)", endpointId, update))
    }
  }

  private fun generateRandomName(): String {
    var name = ""
    val random = Random()
    for (i in 0..4) {
      name += random.nextInt(10)
    }
    return name
  }

  /**
   * Sends a connection request to the endpoint. Either [.onConnectionInitiated] or [.onConnectionFailed] will be called once we've found out
   * if we successfully reached the device.
   */
  fun connectToEndpoint(endpoint: Endpoint) {
    Timber.i("Sending a connection request to endpoint $endpoint")
    // Mark ourselves as connecting so we don't connect multiple times
    isConnecting = true

    // Ask to connect
    connectionsClient
        .requestConnection(getName, endpoint.id, mConnectionLifecycleCallback)
        .addOnFailureListener {
          Timber.w("requestConnection() failed.$it")
          isConnecting = false
          nearbyConnectionsListener?.onConnectionFailed(endpoint)
        }
  }

  /**
   * Returns `true` if we're currently attempting to connect to another device.
   */
  fun isConnecting(): Boolean {
    return isConnecting
  }

  private fun connectedToEndpoint(endpoint: Endpoint) {
    Timber.d(String.format("connectedToEndpoint(endpoint=%s)", endpoint))
    establishedConnections[endpoint.id] = endpoint
    nearbyConnectionsListener?.onEndpointConnected(endpoint)
  }

  private fun disconnectedFromEndpoint(endpoint: Endpoint) {
    Timber.d(String.format("disconnectedFromEndpoint(endpoint=%s)", endpoint))
    establishedConnections.remove(endpoint.id)
    nearbyConnectionsListener?.onEndpointDisconnected(endpoint)
  }

  /**
   * Returns a list of currently connected endpoints.
   */
  fun getDiscoveredEndpoints(): Set<Endpoint> {
    val endpoints = HashSet<Endpoint>()
    endpoints.addAll(mDiscoveredEndpoints.values)
    return endpoints
  }

  /**
   * Returns a list of currently connected endpoints.
   */
  fun getConnectedEndpoints(): Set<Endpoint> {
    val endpoints = HashSet<Endpoint>()
    endpoints.addAll(establishedConnections.values)
    return endpoints
  }

  /**
   * Sends a [Payload] to all currently connected endpoints.
   *
   * @param payload The data you want to send.
   */
  fun send(payload: Payload) {
    send(payload, establishedConnections.keys)
  }

  private fun send(payload: Payload, endpoints: Set<String>) {
    connectionsClient.sendPayload(ArrayList(endpoints), payload)
        .addOnFailureListener { e -> Timber.e("sendPayload() failed.") }
  }
}