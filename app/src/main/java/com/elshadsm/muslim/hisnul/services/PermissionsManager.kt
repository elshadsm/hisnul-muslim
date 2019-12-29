package com.elshadsm.muslim.hisnul.services

import android.Manifest.permission.*
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity

class PermissionsManager(private val activity: DhikrViewActivity) {

  private val permissions = listOf(INTERNET, ACCESS_NETWORK_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
  private val deniedPermissions = mutableListOf<String>()
  @Suppress("PrivatePropertyName")
  private val PERMISSIONS_REQUEST_CODE = 1990

  fun start() {
    val requiredPermissions = mutableListOf<String>()
    permissions.forEach {
      if (ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
        requiredPermissions.add(it)
      }
    }
    if (requiredPermissions.isNotEmpty()) {
      ActivityCompat.requestPermissions(activity as Activity,
          requiredPermissions.toTypedArray(),
          PERMISSIONS_REQUEST_CODE)
    }
  }

  fun handleRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode != PERMISSIONS_REQUEST_CODE) {
      return
    }
    deniedPermissions.clear()
    for (index in grantResults.indices) {
      if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
        deniedPermissions.add(permissions[index])
      }
    }
    if (deniedPermissions.isNotEmpty()) {
      handleDeniedPermission(0)
    }
  }

  private fun handleDeniedPermission(index: Int) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[index])) {
      showExplanation(index)
      return
    }
    askUserToGoToSettings()
  }

  private fun showExplanation(index: Int) {
    val message = String.format(
        activity.getString(R.string.permission_manager_explanation_message),
        getPermissionName(permissions[index]))
    showDialog(message,
        activity.getString(R.string.permission_manager_explanation_label),
        DialogInterface.OnClickListener { dialogInterface, _ ->
          dialogInterface.dismiss()
          start()
        },
        DialogInterface.OnClickListener { dialogInterface, _ ->
          if (index < permissions.size - 1) {
            handleDeniedPermission(index + 1)
          }
          dialogInterface.dismiss()
        })
  }

  private fun askUserToGoToSettings() {
    showDialog(activity.getString(R.string.permission_manager_go_to_settings_message),
        activity.getString(R.string.permission_manager_go_to_settings_label),
        DialogInterface.OnClickListener { dialogInterface, _ ->
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.fromParts("package", activity.packageName, null))
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          activity.startActivity(intent)
          dialogInterface.dismiss()
        },
        DialogInterface.OnClickListener { dialogInterface, _ ->
          dialogInterface.dismiss()
        })
  }

  private fun showDialog(message: String, positiveLabel: String,
                         positiveOnClick: DialogInterface.OnClickListener,
                         negativeOnClick: DialogInterface.OnClickListener) {
    AlertDialog.Builder(activity).apply {
      setTitle(activity.getString(R.string.empty))
      setMessage(message)
      setCancelable(false)
      setPositiveButton(positiveLabel, positiveOnClick)
      setNegativeButton(activity.getString(R.string.permission_manager_dialog_negative_button), negativeOnClick)
      create().show()
    }
  }

  private fun getPermissionName(permission: String) = when (permission) {
    "android.permission.INTERNET" -> activity.getString(R.string.permission_name_internet)
    "android.permission.ACCESS_NETWORK_STATE" -> activity.getString(R.string.permission_name_network_state)
    "android.permission.WRITE_EXTERNAL_STORAGE" -> activity.getString(R.string.permission_name_write_external_storage)
    "android.permission.READ_EXTERNAL_STORAGE" -> activity.getString(R.string.permission_name_read_external_storage)
    else -> ""
  }

}
