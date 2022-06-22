package si.uni_lj.fri.pbd.sensecontext

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.jar.Manifest

const val REQUEST_ID_ACTIVITY_PERMISSIONS = 15

fun MainActivity.requestPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACTIVITY_RECOGNITION)) {
        showRationalDialog(this)
    } else {
        makeRequest(this)
    }
}

fun MainActivity.isPermissionGranted(): Boolean {

    //   For a device running on the system lower than Android Q, return true
    //  For a device running on the Android Q or later, request permission

    return if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        true
    } else {
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}

/**
 * Create and show a rational dialog which explains why is permission needed.
 * Positive button leads to the settings
 */
private fun showRationalDialog(activity: Activity) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
    with(builder) {
        setMessage("permission required for activity recognition")
        setTitle("Permission required")
        setPositiveButton("OK") { p0, p1 -> makeRequest(activity) }
    }

    val dialog = builder.create()
    dialog.show()
}

private fun makeRequest(activity: Activity) {

    ActivityCompat.requestPermissions(activity,
        arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
        REQUEST_ID_ACTIVITY_PERMISSIONS)
}

