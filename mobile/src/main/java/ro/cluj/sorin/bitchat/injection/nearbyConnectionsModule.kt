package ro.cluj.sorin.bitchat.injection

import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.messages.MessagesClient
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import ro.cluj.sorin.bitchat.ui.nearby.ConnectionsManager

val nearbyConnectionsModule = Kodein.Module {
  //  Nearby Connections
  bind<ConnectionsClient>() with provider { Nearby.getConnectionsClient(instance()) }
  bind<MessagesClient>() with provider { Nearby.getMessagesClient(instance()) }
  bind<ConnectionsManager>() with provider { ConnectionsManager(instance()) }
}