package ro.cluj.sorin.bitchat.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.greenspand.kotlin_ext.init
import ro.cluj.sorin.bitchat.R

class GroupLocationMapFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.init(R.layout.fragment_map, container)

}
