package ro.cluj.sorin.bitchat.ui

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

abstract class BaseActivity : AppCompatActivity(), KodeinAware {
  override val kodein by closestKodein()
  val firebaseAuth: FirebaseAuth by instance()
  val db: FirebaseFirestore by instance()
  val sharedPrefs: SharedPreferences by instance()
}