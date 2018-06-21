package ro.cluj.sorin.bitchat.ui.map

import com.google.android.gms.maps.GoogleMap
import ro.cluj.sorin.bitchat.MvpBase

/**
 * Created by sorin on 12.05.18.
 */
interface GroupsMapView : MvpBase.View{
  fun showMap(googleMap: GoogleMap)
}