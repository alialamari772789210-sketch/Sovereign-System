package com.jamesfirstok.aegis.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jamesfirstok.aegis.R
import kotlinx.coroutines.*

class SovereignService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )

    private lateinit var sensorManager: SensorManager

    private var accelX = 0f
    private var accelY = 0f
    private var accelZ = 0f

    companion object {
        const val CHANNEL_ID = "AEGIS_CORE"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startForeground(
            1001,
            buildNotification("AEGIS Sovereign Core Active")
        )

        initializeSensors()

        startTelemetryLoop()
    }

    private fun initializeSensors() {

        sensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )?.also { sensor ->

            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun startTelemetryLoop() {

        serviceScope.launch {

            while (isActive) {

                val telemetry = """
                    X=$accelX
                    Y=$accelY
                    Z=$accelZ
                    TIME=${System.currentTimeMillis()}
                """.trimIndent()

                android.util.Log.i(
                    "AEGIS_TELEMETRY",
                    telemetry
                )

                delay(3000)
            }
        }
    }

    private fun buildNotification(content: String): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AEGIS CORE")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "AEGIS Core Service",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        event?.let {

            accelX = it.values[0]
            accelY = it.values[1]
            accelZ = it.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {

        sensorManager.unregisterListener(this)

        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
