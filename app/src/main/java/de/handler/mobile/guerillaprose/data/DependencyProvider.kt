package de.handler.mobile.guerillaprose.data

import de.handler.mobile.guerillaprose.GuerillaProseApp
import org.koin.dsl.module.Module
import org.koin.dsl.module.module


object DependencyProvider {
    fun createAppModule(guerillaProseApp: GuerillaProseApp): Module {
        // For more information how to build a module check out
        // https://insert-koin.io/docs/1.0/getting-started/android/
        return module {
            single { GuerillaProseProvider() }
            single { GuerillaProseRepository(get()) }
            single { UserProvider() }
            single { UserRepository(get()) }
        }
    }
}
