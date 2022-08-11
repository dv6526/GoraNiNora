package si.uni_lj.fri.pbd.sensecontext

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

const val REQUEST_ID_ACTIVITY_PERMISSIONS = 15
const val  LOCATION_PERMISSION_CODE = 16
const val BACKGROUND_LOCATION_PERMISSIONS_CODE = 17

class HandlePermissions: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()
    }

    fun requestPermissions() {

        if (isPermissionTransitionRecognitionGranted(this)) {
            TrackingHelper.requestActivityTransitionUpdates(this)
            requestPermissionForLocation(this)
        } else {
            requestPermissionTransitionRecognition(this)
        }

    }

    fun requestPermissionTransitionRecognition(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACTIVITY_RECOGNITION)) {
            showRationalDialog(activity)
        } else {
            makeRequestActivityRecognition(activity)
        }
    }

    fun isPermissionTransitionRecognitionGranted(context: Context): Boolean {

        //   For a device running on the system lower than Android Q, return true
        //  For a device running on the Android Q or later, request permission

        return if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            true
        } else {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    fun requestPermissionForLocation(activity: Activity) {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //FINE location permission is granted
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    askForBackgroundPermission(activity)
                }
            }
        } else {
            // ask for fine location permission
            askForFineLocationPermission(activity)

        }
    }

    private fun askForFineLocationPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            showRationalDialogLocationPermission(activity)
        } else {
            makeRequestFineLocation(activity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askForBackgroundPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            showRationalDialogBackgroundPermission(activity)
        } else {
            makeRequestBackgroundLocation(activity)
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
            setPositiveButton("OK") { p0, p1 -> makeRequestActivityRecognition(activity) }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showRationalDialogLocationPermission(activity: Activity) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
        with(builder) {
            setMessage("Location permission needed!")
            setTitle("Permission required")
            setPositiveButton("OK") { p0, p1 -> makeRequestFineLocation(activity)}
        }

        val dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showRationalDialogBackgroundPermission(activity: Activity) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
        with(builder) {
            setMessage("Background Location permission needed!")
            setTitle("Permission required")
            setPositiveButton("OK") { p0, p1 -> makeRequestBackgroundLocation(activity)}
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun makeRequestFineLocation(activity: Activity) {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun makeRequestBackgroundLocation(activity: Activity) {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            BACKGROUND_LOCATION_PERMISSIONS_CODE)
    }

    private fun makeRequestActivityRecognition(activity: Activity) {
        ActivityCompat.requestPermissions(activity,
            arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
            REQUEST_ID_ACTIVITY_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ID_ACTIVITY_PERMISSIONS) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(MainActivity.TAG, "Permission denied")
                finish()
            } else {
                Log.d(MainActivity.TAG, "Permission granted")
                TrackingHelper.requestActivityTransitionUpdates(this)
                requestPermissionForLocation(this)
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionForLocation(this)
            } else {
                finish()
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSIONS_CODE) {
            finish()
        }
    }






}








