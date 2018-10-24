package de.handler.mobile.guerillaprose.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.picasso.Picasso
import de.handler.mobile.guerillaprose.GuerillaProseApp
import okhttp3.OkHttpClient
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import java.util.*


object DependencyProvider {
    fun createAppModule(guerillaProseApp: GuerillaProseApp): Module {
        // For more information how to build a module check out
        // https://insert-koin.io/docs/1.0/getting-started/android/
        return module {
            single { Picasso.Builder(guerillaProseApp).build() }
            single { OkHttpClient().newBuilder().build() }
            single { Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                    .build() }
            single { GuerillaFileProvider(get(), get()) }
            single { GuerillaProseProvider(get(), get()) }
            single { GuerillaProseRepository(get(), get()) }
            single { UserProvider(get(), get()) }
            single { UserRepository(get()) }
            single { FlickrProvider(get(), get()) }
            single { FlickrRepository(get()) }
        }
    }
}
