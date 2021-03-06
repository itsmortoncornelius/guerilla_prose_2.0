package de.handler.mobile.guerillaprose.presentation


import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.NavController
import androidx.navigation.findNavController
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.User
import de.handler.mobile.guerillaprose.data.UserRepository
import kotlinx.android.synthetic.main.fragment_create_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class CreateProfileFragment : OnBackAwareFragment(), CoroutineScope {
    private val job = Job()
    private val userRepository: UserRepository by inject()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = view.findNavController()
        buttonCreateUser.setOnClickListener {
            createUserAndNavigate(navController)
        }
        buttonContinueGuest.setOnClickListener {
            createUserAndNavigate(navController)
        }
        emailTextInputEditText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_GO -> {
                    createUserAndNavigate(navController)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return userRepository.user != null
    }

    private fun createUserAndNavigate(navController: NavController) = launch {
        val firstName = firstNameTextInputEditText.text.toString()
        val lastName = lastNameTextInputEditText.text.toString()
        val email = emailTextInputEditText.text.toString()

        val user = userRepository.createUser(User(firstName, lastName, email)).await()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(MainActivity.KEY_USER_ID, user?.id).apply()

        navController.popBackStack(R.id.fragmentListProse, false)
    }
}
