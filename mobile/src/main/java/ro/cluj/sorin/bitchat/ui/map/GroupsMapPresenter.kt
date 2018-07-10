package ro.cluj.sorin.bitchat.ui.map

import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseUser
import ro.cluj.sorin.bitchat.BasePresenter

/**
 * Created by sorin on 12.05.18.
 */
class GroupsMapPresenter : BasePresenter<GroupsMapView>() {
  fun mapIsReady(googleMap: GoogleMap) {
    view.showMap(googleMap)
  }

  fun userIsLoggedIn(user: FirebaseUser) {
    view.showUserIsLoggedIn(user)
  }

  fun userIsLoggedOut() {
    view.showUserIsLoggedOut()
  }
}