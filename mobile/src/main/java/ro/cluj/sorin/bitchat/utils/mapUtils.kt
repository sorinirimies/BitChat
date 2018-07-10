package ro.cluj.sorin.bitchat.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng

/**
 * Function used to calculate the amount of degrees between this two parameter degrees
 */
fun degreesBetween(b1: Float, b2: Float): Float {
  val nb1 = normalizeBearing(b1)
  val nb2 = normalizeBearing(b2)

  val max = Math.max(nb1, nb2)
  val min = Math.min(nb1, nb2)

  val maxMinusMin = max - min
  val minMinusMax = normalizeBearing(min - max)

  return Math.min(maxMinusMin, minMinusMax)
}

fun normalizeBearing(bearing: Float): Float = (360f + bearing) % 360

fun Location.toLngLatString() = "${this.longitude},${this.latitude}"

fun Location.toLatLng() = LatLng(this.latitude, this.longitude)


fun Context.createLocationRequest(priority: Int, interval: Long): LocationRequest {
  val locationRequest = LocationRequest().apply {
    this.priority = priority
    this.interval = interval
  }
  // Create LocationSettingsRequest object using location request
  val builder = LocationSettingsRequest.Builder()
  builder.addLocationRequest(locationRequest)
  val settingsClient = LocationServices.getSettingsClient(this)
  settingsClient.checkLocationSettings(builder.build())
  return locationRequest
}