package si.uni_lj.fri.pbd.GoraNiNora

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

const val REQUEST_ID_ACTIVITY_PERMISSIONS = 15
const val  LOCATION_PERMISSION_CODE = 16
const val BACKGROUND_LOCATION_PERMISSIONS_CODE = 17

class HandlePermissions {



    companion object {

        lateinit var context: Context
        lateinit var activity: Activity

        fun setActivityAndContext(context: Context, activity: Activity) {
            this.context = context
            this.activity = activity
        }


        fun isPermissionTransitionRecognitionGranted(): Boolean {

            return if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                true
            } else {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                )
            }
        }

        fun isPermissionForFineLocationGranted(): Boolean {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //FINE location permission is granted
                return true
            }
            return false
        }

        fun isPermissionForBackgroundLocationGranted(): Boolean {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                return true
            }
            return false
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun requestPermissionForTransitionRecognition() {
            if (isPermissionTransitionRecognitionGranted()) {
                TrackingHelper.requestActivityTransitionUpdates(context)
            } else {
                askForTransitionPermission()
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun requestPermissionForFineLocation() {
            if (!isPermissionForFineLocationGranted()) {
                askForFineLocationPermission()
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun requestPermissionForBackgroundLocation() {
            if (!isPermissionForBackgroundLocationGranted()) {
                askForBackgroundLocationPermission()
            }
        }



        private fun askForFineLocationPermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationalDialogLocationPermission(activity)
            } else {
                makeRequestFineLocation(activity)
            }
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun askForBackgroundLocationPermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                showRationalDialogBackgroundPermission(activity)
            } else {
                makeRequestBackgroundLocation(activity)
            }
        }

        private fun askForTransitionPermission() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACTIVITY_RECOGNITION)) {
                showRationalDialog(activity)
            } else {
                makeRequestActivityRecognition(activity)
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
    }







}








