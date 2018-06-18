package ro.cluj.sorin.bitchat.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.greenspand.kotlin_ext.init
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

abstract class BaseFragment : Fragment(), KodeinAware {

  override val kodein by closestKodein()

  val firebaseAuth: FirebaseAuth by instance()
  val db: FirebaseFirestore by instance()
  val sharedPrefs: SharedPreferences by instance()

  @LayoutRes abstract fun getLayoutId(): Int

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater.init(getLayoutId(), container)
}