package ro.cluj.sorin.bitchat

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import ro.cluj.sorin.bitchat.injection.firebaseModule
import ro.cluj.sorin.bitchat.injection.nearbyConnectionsModule
import ro.cluj.sorin.bitchat.injection.networkModule
import ro.cluj.sorin.bitchat.injection.uiPresentersModule

/**
 * Created by Sorin Albu-Irimies on 5/18/2018.
 */
class BitChatApplication : Application(), KodeinAware {
  override fun onCreate() {
    super.onCreate()
    FirebaseApp.initializeApp(applicationContext)
    FacebookSdk.sdkInitialize(applicationContext)
  }

  override val kodein = Kodein.lazy {
    import(androidModule(this@BitChatApplication))
    import(networkModule)
    import(nearbyConnectionsModule)
    import(firebaseModule)
    import(uiPresentersModule)
  }
}