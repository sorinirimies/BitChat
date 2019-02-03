package ro.cluj.sorin.bitchat.ui

import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.android.inject

/**
 * Created by sorin on 12.05.18.
 */
abstract class BaseActivity : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth by inject()
    val db: FirebaseFirestore by inject()
}