package ro.cluj.sorin.bitchat.injection

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = Kodein.Module{
  val baseUrl = "https://api.legoomo.com/"
  val moshi: Moshi by lazy {
    Moshi.Builder()
        .build()
  }
  val client by lazy {
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build()
  }
  bind<Retrofit>() with singleton {
    Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(baseUrl)
        .build()
  }
}