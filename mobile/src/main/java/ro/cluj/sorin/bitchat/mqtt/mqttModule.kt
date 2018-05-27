package ro.cluj.sorin.bitchat.mqtt

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

val mqttModule = Kodein.Module {
    bind<MqttManager>() with provider { TotemzMqttManager(instance(), instance()) }
}