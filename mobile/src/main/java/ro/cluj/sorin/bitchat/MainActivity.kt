package ro.cluj.sorin.bitchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ro.cluj.sorin.bitchat.ui.ChatFragment
import ro.cluj.sorin.bitchat.ui.GroupLocationMapFragment
import ro.cluj.sorin.bitchat.ui.UserFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.contFragments, ChatFragment()).commitNow()
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_chat -> supportFragmentManager.beginTransaction().replace(R.id.contFragments, ChatFragment()).commitNow()
                R.id.action_map -> supportFragmentManager.beginTransaction().replace(R.id.contFragments, GroupLocationMapFragment()).commitNow()
                R.id.action_user -> supportFragmentManager.beginTransaction().replace(R.id.contFragments, UserFragment()).commitNow()
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
