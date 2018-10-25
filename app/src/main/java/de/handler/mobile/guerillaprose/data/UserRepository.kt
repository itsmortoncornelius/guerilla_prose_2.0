package de.handler.mobile.guerillaprose.data

import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext


class UserRepository(private val provider: UserProvider): CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    var user: User? = null

    fun createUser(newUser: User): Deferred<User?> {
        return async {
            user = provider.createUser(newUser).await()
            return@async user
        }
    }

    fun getUser(id: String): Deferred<User?> {
        return async {
            return@async provider.getUser(id).await()
        }
    }
}