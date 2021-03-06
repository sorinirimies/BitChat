package ro.cluj.sorin.bitchat.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.javax.inject.Singleton

/**
 * You can use like this.
 * val channel = EventBus().asChannel<ItemChangeAction>()
 * launch (UI){
 *   for(action in channel){
 *     // You can use item
 *     action.item
 *   }
 * }
 */
@Singleton
class EventBus @Inject constructor() {
    val bus: BroadcastChannel<Any> = ConflatedBroadcastChannel()

    fun send(o: Any) {
        GlobalScope.launch {
            bus.send(o)
        }
    }

    inline fun <reified T> asChannel(): ReceiveChannel<T> {
        return bus.openSubscription().filter { it is T }.map { it as T }
    }
}