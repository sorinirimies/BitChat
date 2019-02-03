package ro.cluj.sorin.bitchat.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.CallSuper
import com.google.firebase.auth.FirebaseAuth
import com.sorinirimies.kotlinx.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import ro.cluj.sorin.bitchat.BaseFragAdapter
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.ui.groups.GroupsFragment
import ro.cluj.sorin.bitchat.ui.map.GroupsMapFragment
import ro.cluj.sorin.bitchat.ui.user.UserProfileFragment
import ro.cluj.sorin.bitchat.ui.user.UserSettingsListener
import ro.cluj.sorin.bitchat.utils.FadePageTransformer
import ro.cluj.sorin.bitchat.utils.hasPermissions
import ro.cluj.sorin.bitchat.utils.onPageSelected

private const val PAGE_CHAT = 0
private const val PAGE_MAP = 1
private const val PAGE_USER = 2
private const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

class MainActivity : BaseActivity(), MainView, UserSettingsListener {

    private val presenter: MainPresenter by inject()
    private var isLoggedIn = false
    private val fragAdapter by lazy {
        BaseFragAdapter(supportFragmentManager,
                arrayListOf(GroupsFragment(), GroupsMapFragment(), UserProfileFragment()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
        /* Instantiate pager adapter and set fragments*/
        pagerContFragments.apply {
            setPageTransformer(true, FadePageTransformer())
            this.adapter = fragAdapter
            offscreenPageLimit = 3
            onPageSelected {
                when (it) {
                    PAGE_CHAT -> bottomNavigation.selectedItemId = R.id.action_chat_groups
                    PAGE_MAP -> bottomNavigation.selectedItemId = R.id.action_map
                    PAGE_USER -> bottomNavigation.selectedItemId = R.id.action_user
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
        pagerContFragments.currentItem = PAGE_USER
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(this, *getRequiredPermissions())) {
            requestPermissions(getRequiredPermissions(), REQUEST_CODE_REQUIRED_PERMISSIONS)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun enableNearbyChat(shouldEnable: Boolean) {
        if (fragAdapter.getItem(0) is GroupsFragment) {
            (fragAdapter.getItem(0) as GroupsFragment).enableNearbyChatGroup(shouldEnable)
        }
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (grantResult in grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    toast(getString(R.string.error_missing_permissions))
                    finish()
                    return
                }
            }
            recreate()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * An optional hook to pool any permissions the app needs with the permissions ConnectionsActivity
     * will request.
     *
     * @return All permissions required for the app to properly function.
     */
    private fun getRequiredPermissions(): Array<String> {
        return REQUIRED_PERMISSIONS
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
                        //TODO handle something if needed
                    }
                    "facebook.com" -> {
                        //TODO handle something if needed
                    }
                }
            }
        } else {
            isLoggedIn = false
        }
    }
}
