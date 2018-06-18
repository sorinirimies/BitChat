package ro.cluj.sorin.bitchat.ui.map

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.greenspand.kotlin_ext.init
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.LazyKodein
import org.kodein.di.android.closestKodein
import ro.cluj.sorin.bitchat.R
import ro.cluj.sorin.bitchat.ui.BaseFragment

class UsersLocationMapFragment : BaseFragment() , KodeinAware{
    override val kodein by closestKodein()

    override fun getLayoutId() = R.layout.fragment_map

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }

}
