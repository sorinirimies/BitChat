package ro.cluj.sorin.bitchat.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.flags.impl.SharedPreferencesFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sorinirimies.kotlinx.init
import org.koin.android.ext.android.inject

abstract class BaseFragment : Fragment() {

    val firebaseAuth: FirebaseAuth by inject()
    val db: FirebaseFirestore by inject()
    val sharedPrefs: SharedPreferences by lazy {
        SharedPreferencesFactory.getSharedPreferences(requireContext())
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.init(getLayoutId(), container)
}