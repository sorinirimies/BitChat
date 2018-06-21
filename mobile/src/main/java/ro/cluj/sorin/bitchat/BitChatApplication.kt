package ro.cluj.sorin.bitchat

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import ro.cluj.sorin.bitchat.injection.firebaseModule
import ro.cluj.sorin.bitchat.injection.networkModule
import ro.cluj.sorin.bitchat.injection.uiPresentersModule
import ro.cluj.sorin.bitchat.injection.mqttModule

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
    import(mqttModule)
    import(networkModule)
    import(firebaseModule)
    import(uiPresentersModule)
  }
}