package de.handler.mobile.guerillaprose.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import de.handler.mobile.guerillaprose.R
import de.handler.mobile.guerillaprose.data.User
import de.handler.mobile.guerillaprose.data.UserRepository
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.coroutines.experimental.CoroutineContext

class ShowProfileFragment : Fragment(), CoroutineScope {
    private val job = Job()
    private val userRepository: UserRepository by inject()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = sharedPreferences.getString(MainActivity.KEY_USER_ID, null)

        val navigationController = view.findNavController()

        launch {
            if (userId != null) {
                val user = userRepository.getUser(userId).await()
                when (user) {
                    null -> Timber.e("no user with userId $userId can be found")
                    else -> bind(user)
                }
            } else {
                Timber.e("userId is null")
                navigationController.popBackStack()
                navigationController.navigate(R.id.actionCreateProfile)
            }
        }

        buttonUpdateUser.setOnClickListener {
            launch {
                userRepository.updateUser(User(
                        firstNameEditText.text.toString(),
                        lastNameEditText.text.toString(),
                        emailEditText.text.toString(),
                        userId
                )).await()
                AlertDialog.Builder(context!!)
                        .setIcon(R.drawable.ic_check)
                        .setMessage(R.string.success_update_user)
                        .setPositiveButton(android.R.string.ok, null)
                        .create()
                        .show()
            }
        }

        buttonDeleteUser.setOnClickListener {
            launch {
                if (userId != null) {
                    userRepository.deleteUser(userId).await()
                    navigationController.popBackStack()
                    navigationController.navigate(R.id.actionCreateProfile)
                } else {
                    Timber.e("userId is null")
                    navigationController.popBackStack()
                    navigationController.navigate(R.id.actionCreateProfile)
                }
            }
        }
    }

    private fun bind(user: User) {
        firstNameEditText.setText(user.firstname)
        lastNameEditText.setText(user.lastname)
        emailEditText.setText(user.email)
    }
}
