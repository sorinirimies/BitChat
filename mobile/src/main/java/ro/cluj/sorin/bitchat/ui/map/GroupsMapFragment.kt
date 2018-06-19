package ro.cluj.sorin.bitchat.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.markodevcic.peko.Peko
import kotlinx.android.synthetic.main.fragment_map.mapBitchat
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.ui.BaseFragment
import ro.cluj.sorin.bitchat.utils.loadMapStyle

class GroupsMapFragment : BaseFragment(), KodeinAware, GroupsMapView {

  override val kodein by closestKodein()
  private val presenter: GroupsMapPresenter by instance()
  override fun getLayoutId() = R.layout.fragment_map
  private var isMapReady = false
  private var googleMap: GoogleMap? = null
  private lateinit var mapView: MapView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mapView = view.findViewById(R.id.mapBitchat)
    mapView.onCreate(savedInstanceState)
    mapView.onResume()
    presenter.attachView(this)
    activity?.let {
      if (Peko.isRequestInProgress()) {
        launch(UI) {
          val result = Peko.resultDeferred?.await()
        }
      }
      launch(UI) {
        val permissionResultDeferred = Peko.requestPermissionsAsync(it, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        val (result) = permissionResultDeferred.await()
        if (Manifest.permission.ACCESS_FINE_LOCATION in result
            && Manifest.permission.ACCESS_COARSE_LOCATION in result) {
          mapBitchat.getMapAsync(onMapReady)
        }
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

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }
}
