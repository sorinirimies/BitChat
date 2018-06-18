package ro.cluj.sorin.bitchat.ui

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.bottomNavigation
import kotlinx.android.synthetic.main.activity_main.pagerContFragments
import kotlinx.coroutines.experimental.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ro.cluj.sorin.bitchat.BaseFragAdapter
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.ui.groups.GroupsFragment
import ro.cluj.sorin.bitchat.ui.map.UsersLocationMapFragment
import ro.cluj.sorin.bitchat.ui.user.UserProfileFragment
import ro.cluj.sorin.bitchat.utils.EventBus
import ro.cluj.sorin.bitchat.utils.FadePageTransformer
import ro.cluj.sorin.bitchat.utils.onPageSelected
import timber.log.Timber

private const val PAGE_CHAT = 0
private const val PAGE_MAP = 1
private const val PAGE_USER = 2

class MainActivity : AppCompatActivity(), KodeinAware, MainView {
  override val kodein by closestKodein()
  private val firebaseAuth: FirebaseAuth by instance()
  private val presenter: MainPresenter by instance()
  private val sharedPrefs: SharedPreferences by instance()
  private var isLoggedIn = false
  private val channel = EventBus().asChannel<Any>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    presenter.attachView(this)
    /* Instantiate pager adapter and set fragments*/
    pagerContFragments.apply {
      setPageTransformer(true, FadePageTransformer())
      this.adapter = BaseFragAdapter(supportFragmentManager,
          arrayListOf(GroupsFragment(), UsersLocationMapFragment(), UserProfileFragment()))
      offscreenPageLimit = 3
      onPageSelected {
        when (it) {
          PAGE_CHAT -> bottomNavigation.selectedItemId =  R.id.action_chat_groups
          PAGE_MAP -> bottomNavigation.selectedItemId =  R.id.action_map
          PAGE_USER -> bottomNavigation.selectedItemId =  R.id.action_user
        }
      }
    }
    supportFragmentManager.beginTransaction().add(R.id.pagerContFragments, GroupsFragment()).commitNow()
    bottomNavigation.setOnNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.action_chat_groups -> pagerContFragments.currentItem = PAGE_CHAT
        R.id.action_map -> pagerContFragments.currentItem = PAGE_MAP
        R.id.action_user -> pagerContFragments.currentItem = PAGE_USER
      }
      return@setOnNavigationItemSelectedListener true
    }
  }

  override fun onStart() {
    super.onStart()
    firebaseAuth.addAuthStateListener(authStateListener)
    launch {
      for (item in channel) {
        when (item) {
          is Location -> Timber.w("Channel message is: ${item.altitude}")
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    firebaseAuth.removeAuthStateListener(authStateListener)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.detachView()
  }

  private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
    val user = firebaseAuth.currentUser
    if (user != null) {
      isLoggedIn = true
      for (userInfo in user.providerData) {
        when (userInfo.providerId) {
          "google.com" -> {
            val token = sharedPrefs.getString("GOOGLE_TOKEN", user.getToken(true).toString())
          }
          "facebook.com" -> {
            val token = sharedPrefs.getString("FACEBOOK_TOKEN", user.getToken(true).toString())
          }
        }
      }
    } else {
      isLoggedIn = false
    }
  }
}
