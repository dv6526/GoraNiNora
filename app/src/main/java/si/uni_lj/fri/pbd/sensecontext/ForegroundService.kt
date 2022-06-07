package si.uni_lj.fri.pbd.sensecontext

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ForegroundService : Service(), SensorEventListener {

    companion object {
        const val ACTION_STOP = "action_stop"
        const val ACTION_START = "action_start"
        var IS_RUNNING: Boolean = false
        const val NOTIFICATION_ID = 12
        private const val CHANNEL_ID: String = "Sensor Data"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor

    override fun onBind(intent: Intent): IBinder? {
        //TODO("Return the communication channel to the service.")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        IS_RUNNING = true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun createNotification(): Notification {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sensing accelometer data")
            .setContentText("running in foreground")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setChannelId(CHANNEL_ID)

        return builder.build()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action.equals(ACTION_STOP)) {
            stopSelf()
        } else if (intent.action.equals(ACTION_START)) {
            startSensing()
        }
        return START_NOT_STICKY
    }

    fun startSensing() {
        Log.e("service", "Service is running...")
        // initialize sensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // get sensor from sensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?:return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        Log.e("service", "$x $y $x")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        Log.e("service", "service is destroyed")
        sensorManager.unregisterListener(this)
        super.onDestroy()
        IS_RUNNING = false
    }

    private fun stopService() {
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()

        IS_RUNNING = false
    }



}