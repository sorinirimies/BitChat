package ro.cluj.sorin.bitchat.utils

import android.support.v4.view.ViewPager

interface BitChatViewPagerChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) = Unit
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
    override fun onPageSelected(position: Int) = Unit
}

inline fun ViewPager.onPageSelected(crossinline pageSelected: (Int) -> Unit) {
    addOnPageChangeListener(object : BitChatViewPagerChangeListener {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pageSelected.invoke(position)
        }
    })
}