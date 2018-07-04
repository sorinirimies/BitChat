package ro.cluj.sorin.bitchat.ui.map

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_map.mapBitchat
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.ui.BaseFragment
import ro.cluj.sorin.bitchat.utils.createAndAddMarker
import ro.cluj.sorin.bitchat.utils.createLocationRequest
import ro.cluj.sorin.bitchat.utils.hasPermissions
import ro.cluj.sorin.bitchat.utils.loadMapStyle

/**
 * Created by sorin on 12.05.18.
 */
private const val DEFAULT_ZOOM = 13f
private const val LOCATION_UPDATE_INTERVAL = 10L * 1000L

class GroupsMapFragment : BaseFragment(), KodeinAware, GroupsMapView {

  private val fusedLocationClient by lazy { activity?.let { LocationServices.getFusedLocationProviderClient(it) } }
  override val kodein by closestKodein()
  override fun getLayoutId() = R.layout.fragment_map
  private val presenter: GroupsMapPresenter by instance()
  private var isMapReady = false
  private var googleMap: GoogleMap? = null
  private lateinit var mapView: MapView
  private val channelLocation by lazy { BroadcastChannel<Location>(100000) }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mapView = view.findViewById(R.id.mapBitchat)
    mapView.onCreate(savedInstanceState)
    mapView.onResume()
    presenter.attachView(this)
    activity?.let {
      if (hasPermissions(it,
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION)) {
        mapBitchat.getMapAsync(onMapReady)
      }
    }

    launch(UI) {
      channelLocation.openSubscription().consumeEach {
        getLocationAndAnimateMarker(it)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    presenter.detachView()
  }

  private val onMapReady = OnMapReadyCallback {
    presenter.mapIsReady(it)
  }

  @SuppressLint("MissingPermission")
  override fun showMap(googleMap: GoogleMap) {
    isMapReady = true
    this.googleMap = googleMap
    googleMap.apply {
      isMyLocationEnabled = true
      context?.let { loadMapStyle(it, R.raw.google_map_style) }
    }
  }

  private fun getLocationAndAnimateMarker(location: Location) {
    val latLng = LatLng(location.latitude, location.longitude)
    googleMap?.apply {
      createAndAddMarker(latLng, R.mipmap.ic_map_marker)
      moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
    }
  }

  private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
      launch(UI) {
        channelLocation.send(locationResult.lastLocation)
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun onResume() {
    super.onResume()
    mapView.onResume()
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

  override fun onPause() {
    super.onPause()
    mapView.onPause()
    fusedLocationClient?.removeLocationUpdates(locationCallback)
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
    channelLocation.close()
  }
}
