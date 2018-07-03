package ro.cluj.sorin.bitchat.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ro.cluj.sorin.bitchat.R

fun Context.getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
  val canvas = Canvas()
  val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  canvas.setBitmap(bitmap)
  drawable.setBounds(0, 0, resources.getDimension(R.dimen.grid5).toInt(),
      resources.getDimension(R.dimen.grid5).toInt())
  drawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}