package ro.cluj.sorin.bitchat.utils

import android.content.Context
import android.content.res.Resources
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

fun GoogleMap.createAndAddMarker(latLng: LatLng, @DrawableRes markerResource: Int) {
  this.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(markerResource)))
}

fun GoogleMap.loadMapStyle(context: Context, @RawRes style: Int) {
  try {
    val success = setMapStyle(MapStyleOptions.loadRawResourceStyle(context, style))
    if (!success) {
      Timber.e("Map styling failed !!!!")
    }
  } catch (e: Resources.NotFoundException) {
    Timber.e("Map style not found !!!!")
  }
}