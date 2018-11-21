package ro.cluj.sorin.bitchat.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_map.mapBitchat
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.model.ChatGroup
import ro.cluj.sorin.bitchat.model.User
import ro.cluj.sorin.bitchat.model.UserLocation
import ro.cluj.sorin.bitchat.ui.BaseFragment
import ro.cluj.sorin.bitchat.ui.chat.ChatActivity
import ro.cluj.sorin.bitchat.ui.chat.PARAM_CHAT_GROUP
import ro.cluj.sorin.bitchat.ui.groups.DEFAULT_GROUP_ID
import ro.cluj.sorin.bitchat.utils.createLocationRequest
import ro.cluj.sorin.bitchat.utils.hasPermissions
import ro.cluj.sorin.bitchat.utils.loadMapStyle
import ro.cluj.sorin.bitchat.utils.toBitChatUser
import ro.cluj.sorin.bitchat.utils.toLatLng

/**
 * Created by sorin on 12.05.18.
 */
private const val DEFAULT_ZOOM = 12f
private const val LOCATION_UPDATE_INTERVAL = 10L * 1000L

class GroupsMapFragment : BaseFragment(), GroupsMapView {
  override fun showUserIsLoggedIn(user: FirebaseUser) {
    launch(UI) {
      channelFirebaseUser.send(user)
    }
  }

  override fun showUserIsLoggedOut() {
    fusedLocationClient?.removeLocationUpdates(locationCallback)
    channelLocation.close()
    googleMap?.clear()
  }

  override fun getLayoutId() = R.layout.fragment_map
  override val kodein by closestKodein()
  private val fusedLocationClient by lazy { activity?.let { LocationServices.getFusedLocationProviderClient(it) } }
  private val presenter: GroupsMapPresenter by instance()
  private var isMapReady = false
  private var user: User? = null
  private var googleMap: GoogleMap? = null
  private lateinit var mapView: MapView

  private val channelLocation: SendChannel<Location> = actor(UI) {
    channel.consumeEach { location ->
      user?.let { user ->
        locationRef.document(user.id).set(UserLocation(user.id, location.latitude, location.longitude))
      }
    }
  }
  private val channelFirebaseUser by lazy { BroadcastChannel<FirebaseUser>(1) }

  private val locationRef by lazy { db.collection("location") }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mapView = view.findViewById(R.id.mapBitchat)
    mapView.onCreate(savedInstanceState)
    mapView.onResume()
    presenter.attachView(this)
    firebaseAuth.addAuthStateListener(authStateListener)
    activity?.let {
      if (hasPermissions(it,
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION)) {
        mapBitchat.getMapAsync { presenter.mapIsReady(it) }
      }
    }
    startUserChannel()
  }

  private fun startUserChannel() {
    launch(UI) {
      channelFirebaseUser.openSubscription().consumeEach {
        user = it.toBitChatUser()
        registerForUsersLocationUpdates()
      }
    }
  }

  private fun registerForUsersLocationUpdates() {
    locationRef.addSnapshotListener { querySnapshot, firebaseFireStoreException ->
      if (firebaseFireStoreException != null) return@addSnapshotListener
      googleMap?.clear()
      querySnapshot?.forEach {
        val data = it.data
        val userId = data["userId"].toString()
        if (user != null && user?.id != userId) {
          val lat = data["lat"].toString().toDouble()
          val lng = data["lng"].toString().toDouble()
          googleMap?.addMarker(MarkerOptions()
              .position(LatLng(lat, lng))
              .title(user?.name))
              ?.showInfoWindow()
        }
      }
    }
  }

  private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
    val user = firebaseAuth.currentUser
    if (user != null) {
      presenter.userIsLoggedIn(user)
    } else {
      presenter.userIsLoggedOut()
    }
  }

  @SuppressLint("MissingPermission")
  override fun showMap(googleMap: GoogleMap) {
    isMapReady = true
    this.googleMap = googleMap
    googleMap.apply {
      isMyLocationEnabled = true
      context?.let {
        loadMapStyle(it, R.raw.google_map_style)
        moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation?.toLatLng() ?: LatLng(0.00, 0.00), DEFAULT_ZOOM))
        setOnInfoWindowClickListener {
          startChatActivity()
        }
        setOnMarkerClickListener {
          startChatActivity()
          false
        }
      }
    }
  }

  private fun startChatActivity() = startActivity(
      Intent(activity, ChatActivity::class.java).apply {
        putExtra(PARAM_CHAT_GROUP, ChatGroup(DEFAULT_GROUP_ID,
            getString(R.string.group_cryptonarii)))
      })

  private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
      launch {
        channelLocation.send(locationResult.lastLocation)
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun onStart() {
    super.onStart()
    mapView.onStart()
    activity?.let {
      if (hasPermissions(it, Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION)) {
        fusedLocationClient?.requestLocationUpdates(context?.createLocationRequest(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL),
            locationCallback,
            Looper.myLooper())
      }
    }
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
    fusedLocationClient?.removeLocationUpdates(locationCallback)
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()

  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    firebaseAuth.removeAuthStateListener(authStateListener)
    channelFirebaseUser.close()
    channelLocation.close()
    presenter.detachView()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }
}

