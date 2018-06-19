package ro.cluj.sorin.bitchat.ui.map

import com.google.android.gms.maps.GoogleMap
import ro.cluj.sorin.bitchat.MvpBase

interface GroupsMapView : MvpBase.View{
  fun showMap(googleMap: GoogleMap)
}