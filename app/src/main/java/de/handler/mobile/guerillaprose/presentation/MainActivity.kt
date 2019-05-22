package de.handler.mobile.guerillaprose.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.UserRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

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

    override fun onBackPressed() {
        if (invokeBackOrUp()) {
            super.onBackPressed()
        }
    }

    private fun invokeBackOrUp(): Boolean {
        var invokeBack = true
        for (fragment in supportFragmentManager.fragments) {
            for (childFragment in fragment.childFragmentManager.fragments) {
                if (childFragment is OnBackAwareFragment) {
                    invokeBack = childFragment.onBackPressed()
                }
            }
        }
        return invokeBack
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.mainNavigationFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentCreateProfile -> bottomNavigationView.visibility = View.GONE
                else -> bottomNavigationView.visibility = View.VISIBLE
            }
        }

        setupActionBarWithNavController(this, navController)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupUser() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId.isNullOrEmpty()) {
            navigateToCreateProfile()
        } else {
            launch {
                when (userRepository.getUser(userId).await()) {
                    null -> navigateToCreateProfile()
                    else -> navController.navigate(R.id.actionListProse)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = invokeBackOrUp() && navController.navigateUp()

    private fun navigateToCreateProfile() {
        navController.navigate(R.id.actionCreateProfile)
    }


    companion object {
        const val KEY_USER_ID = "key_user_id"
    }
}
