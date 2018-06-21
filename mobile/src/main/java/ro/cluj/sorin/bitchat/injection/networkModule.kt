package ro.cluj.sorin.bitchat.injection

import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Connections
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ro.cluj.sorin.bitchat.mqtt.BitChatManager
import ro.cluj.sorin.bitchat.mqtt.MqttManager

/**
 * Created by sorin on 12.05.18.
 */
val networkModule = Kodein.Module {
  //REST
  val baseUrl = "https://localhost:5000"
  val moshi: Moshi by lazy {
    Moshi.Builder()
        .build()
  }
  val client by lazy {
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()
  }
  bind<Retrofit>() with singleton {
    Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(baseUrl)
        .build()
  }

  //  MQTT
  bind<MqttManager>() with provider { BitChatManager(instance(), instance()) }

  //  Nearby Connections
  bind<ConnectionsClient>() with singleton { Nearby.getConnectionsClient(instance()) }
}