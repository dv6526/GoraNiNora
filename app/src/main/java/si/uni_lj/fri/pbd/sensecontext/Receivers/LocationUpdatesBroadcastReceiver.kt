package si.uni_lj.fri.pbd.sensecontext.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import si.uni_lj.fri.pbd.sensecontext.MainActivity.Companion.TAG
import java.util.*
import java.util.concurrent.Executors

class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS_UPDATES =
            "si.uni_lj.fri.pbd.sensecontext.Receivers.LocationUpdatesBroadcastReceiver." +
                    "PROCESS_UPDATES"
    }


    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == ACTION_PROCESS_UPDATES) {
            // Checks for location availability changes.
            LocationAvailability.extractLocationAvailability(intent)?.let { locationAvailability ->
                if (!locationAvailability.isLocationAvailable) {
                    Log.d(TAG, "Location services are no longer available!")
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->

                    Toast.makeText(context, location.latitude.toString() + " " + location.longitude.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}