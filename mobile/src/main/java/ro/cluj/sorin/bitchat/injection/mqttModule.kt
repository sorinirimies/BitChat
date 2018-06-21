package ro.cluj.sorin.bitchat.injection

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import ro.cluj.sorin.bitchat.mqtt.MqttManager
import ro.cluj.sorin.bitchat.mqtt.TotemzMqttManager

/**
 * Created by sorin on 12.05.18.
 */
val mqttModule = Kodein.Module {
    bind<MqttManager>() with provider {
        TotemzMqttManager(instance(), instance())
    }
}