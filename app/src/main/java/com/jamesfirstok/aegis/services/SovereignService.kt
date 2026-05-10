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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jamesfirstok.aegis.R
import com.jamesfirstok.aegis.core.sensors.SensorFusionEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SovereignService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    )

    private lateinit var sensorManager: SensorManager

    private val fusionEngine = SensorFusionEngine()

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
            buildNotification(
                "AEGIS Sovereign Core Active"
            )
        )

        initializeSensors()

        startTelemetryLoop()

        Log.i(
            "AEGIS_CORE",
            "Sovereign Service Started"
        )
    }

    private fun initializeSensors() {

        sensorManager =
            getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager

        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )?.also { sensor ->

            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        Log.i(
            "AEGIS_SENSORS",
            "Accelerometer Initialized"
        )
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

                Log.i(
                    "AEGIS_TELEMETRY",
                    telemetry
                )

                delay(3000)
            }
        }
    }

    private fun buildNotification(
        content: String
    ): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("AEGIS CORE")
            .setContentText(content)
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
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
                getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        event?.let {

            accelX = it.values[0]
            accelY = it.values[1]
            accelZ = it.values[2]

            val state =
                fusionEngine.processAccelerometer(
                    accelX,
                    accelY,
                    accelZ
                )

            Log.i(
                "AEGIS_FUSION",
                """
                STATUS=${state.status}
                THREAT=${state.threatScore}
                MOTION=${state.motionIntensity}
                """.trimIndent()
            )
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }

    override fun onDestroy() {

        sensorManager.unregisterListener(this)

        serviceScope.cancel()

        Log.i(
            "AEGIS_CORE",
            "Sovereign Service Destroyed"
        )

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null
}
