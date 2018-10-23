package de.handler.mobile.guerillaprose.presentation

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity


object PermissionManager {
    fun permissionPending(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                permission) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: FragmentActivity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                requestCode)
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