package ro.cluj.sorin.bitchat.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by sorin on 12.05.18.
 */
public  class FadePageTransformer implements ViewPager.PageTransformer {
    public void transformPage(View view, float position) {

        if (position <= -1.0F || position >= 1.0F) {        // [-Infinity,-1) OR (1,+Infinity]
            view.setAlpha(0.0F);
            view.setVisibility(View.GONE);
        } else if( position == 0.0F ) {     // [0]
            view.setAlpha(1.0F);
            view.setVisibility(View.VISIBLE);
        } else {

            // Position is between [-1,1]
            view.setAlpha(1.0F - Math.abs(position));
            view.setTranslationX(-position * (view.getWidth() / 2));
            view.setVisibility(View.VISIBLE);
        }
    }

}