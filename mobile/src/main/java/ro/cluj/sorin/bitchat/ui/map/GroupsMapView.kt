package ro.cluj.sorin.bitchat.ui.map

import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.MvpBase

/**
 * Created by sorin on 12.05.18.
 */
interface GroupsMapView : MvpBase.View {

  fun showMap(googleMap: GoogleMap)

  fun showUserIsLoggedIn(user: FirebaseUser)

  fun showUserIsLoggedOut()
}