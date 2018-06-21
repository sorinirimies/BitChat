package ro.cluj.sorin.bitchat

/* ktlint-disable no-wildcard-imports */

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by Sorin Albu-Irimies on 5/18/2018.
 */
class BaseFragAdapter(fm: FragmentManager, private var fragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

  override fun getItem(position: Int): Fragment? {
    return this.fragments[position]
  }

  override fun getCount(): Int {
    return fragments.size
  }
}

