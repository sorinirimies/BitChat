package ro.cluj.sorin.bitchat.injection

import com.google.android.gms.nearby.Nearby
import org.koin.dsl.module
import ro.cluj.sorin.bitchat.ui.nearby.ConnectionsManager

val nearbyConnectionsModule = module {
    //  Nearby Connections
    factory { Nearby.getConnectionsClient(get()) }
    factory { Nearby.getMessagesClient(get()) }
    factory { ConnectionsManager(get()) }
}