package com.jamesfirstok.aegis.core.sensors

data class SensorState(

    val accelerationX: Float,

    val accelerationY: Float,

    val accelerationZ: Float,

    val motionIntensity: Float,

    val threatScore: Int,

    val status: String
)
