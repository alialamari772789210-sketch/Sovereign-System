package com.jamesfirstok.aegis.core.sensors

import kotlin.math.abs
import kotlin.math.sqrt

class SensorFusionEngine {

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private var motionLevel = 0f

    fun processAccelerometer(
        x: Float,
        y: Float,
        z: Float
    ): SensorState {

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        lastX = x
        lastY = y
        lastZ = z

        motionLevel = sqrt(
            (deltaX * deltaX) +
            (deltaY * deltaY) +
            (deltaZ * deltaZ)
        )

        val threatScore = calculateThreatScore()

        return SensorState(
            accelerationX = x,
            accelerationY = y,
            accelerationZ = z,
            motionIntensity = motionLevel,
            threatScore = threatScore,
            status = evaluateStatus(threatScore)
        )
    }

    private fun calculateThreatScore(): Int {

        return when {

            motionLevel > 20f -> 90

            motionLevel > 12f -> 70

            motionLevel > 7f -> 45

            motionLevel > 3f -> 20

            else -> 5
        }
    }

    private fun evaluateStatus(score: Int): String {

        return when {

            score >= 80 -> "CRITICAL"

            score >= 50 -> "HIGH_ACTIVITY"

            score >= 25 -> "MODERATE"

            else -> "STABLE"
        }
    }
}
