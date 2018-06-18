package ro.cluj.sorin.bitchat.injection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

val firebaseModule = Kodein.Module {
  bind<FirebaseFirestore>() with singleton { FirebaseFirestore.getInstance() }
  bind<FirebaseAuth>() with singleton { FirebaseAuth.getInstance() }
}