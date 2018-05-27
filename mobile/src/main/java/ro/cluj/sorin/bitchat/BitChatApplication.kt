package ro.cluj.sorin.bitchat

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import ro.cluj.sorin.bitchat.mqtt.mqttModule

class BitChatApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidModule(this@BitChatApplication))
        import(mqttModule)
    }
}