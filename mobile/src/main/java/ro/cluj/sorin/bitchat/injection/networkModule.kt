package ro.cluj.sorin.bitchat.injection

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ro.cluj.sorin.bitchat.mqtt.BitChatMqttManager

/**
 * Created by sorin on 12.05.18.
 */
val networkModule = module {

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
    single {
        Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(baseUrl)
                .build()
    }
    //  MQTT
    factory { BitChatMqttManager(get(), get()) }
}