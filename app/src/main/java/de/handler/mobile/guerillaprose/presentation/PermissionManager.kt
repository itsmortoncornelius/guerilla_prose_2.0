package de.handler.mobile.guerillaprose.presentation

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


object PermissionManager {
    fun permissionPending(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
                context,
                permission) != PackageManager.PERMISSION_GRANTED
    }

    // for activities
    fun requestPermission(activity: FragmentActivity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                requestCode)
    }

    // for fragments
    fun requestPermissions(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    fun handlePermissionsResult(activity: FragmentActivity,
                                grantResults: IntArray,
                                permissions: Array<out String>,
                                action: ((Boolean) -> Unit)) {
        action(grantResults.isNotEmpty()
                && permissions.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]))
    }
}