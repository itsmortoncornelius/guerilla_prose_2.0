package de.handler.mobile.guerillaprose.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.UserRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.experimental.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val job = Job()
    private val userRepository: UserRepository by inject()
    private lateinit var navController: NavController

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
        setupUser()
    }



    private fun setupNavigation() {
        navController = findNavController(R.id.mainNavigationFragment)

        setupActionBarWithNavController(this, navController)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupUser() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId.isNullOrEmpty()) {
            navController.navigate(R.id.actionCreateProfile)
        } else {
            userId?.let {
                launch {
                    val user = userRepository.getUser(it).await()
                    when (user) {
                        null -> navController.navigate(R.id.actionCreateProfile)
                        else -> navController.navigate(R.id.actionListProse)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    companion object {
        const val KEY_USER_ID = "key_user_id"
    }
}
