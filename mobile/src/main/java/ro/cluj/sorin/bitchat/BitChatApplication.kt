package ro.cluj.sorin.bitchat

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import org.koin.core.context.startKoin
import ro.cluj.sorin.bitchat.injection.firebaseModule
import ro.cluj.sorin.bitchat.injection.nearbyConnectionsModule
import ro.cluj.sorin.bitchat.injection.networkModule
import ro.cluj.sorin.bitchat.injection.uiPresentersModule
import timber.log.Timber

/**
 * Created by Sorin Albu-Irimies on 5/18/2018.
 */
class BitChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        FacebookSdk.sdkInitialize(applicationContext)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            logger()
            modules(firebaseModule,
                    nearbyConnectionsModule,
                    networkModule,
                    uiPresentersModule)
        }
    }
}