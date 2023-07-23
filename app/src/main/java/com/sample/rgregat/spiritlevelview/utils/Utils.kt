package com.sample.rgregat.spiritlevelview.utils

import android.view.Display
import android.view.Surface
import kotlin.math.pow
import kotlin.math.roundToInt

class Utils {
    companion object {
        fun toDegree(radianValue: Double): Double {
            return radianValue * (180 / Math.PI)
        }

        fun adjustToDisplayRotation(display: Display, sensorData: FloatArray): FloatArray {
            val adjustedSensorData = FloatArray(3)

            when (display.rotation) {
                Surface.ROTATION_0 -> {
                    // Portrait-Mode
                    adjustedSensorData[0] = sensorData[0]
                    adjustedSensorData[1] = sensorData[1]
                    adjustedSensorData[2] = sensorData[2]
                }

                Surface.ROTATION_90 -> {
                    // Landscape mode (rotated to the left)
                    adjustedSensorData[0] = sensorData[1]
                    adjustedSensorData[1] = -sensorData[0]
                    adjustedSensorData[2] = sensorData[2]
                }

                Surface.ROTATION_180 -> {
                    // Portrait mode (rotated to the left)
                    adjustedSensorData[0] = -sensorData[0]
                    adjustedSensorData[1] = -sensorData[1]
                    adjustedSensorData[2] = sensorData[2]
                }

                Surface.ROTATION_270 -> {
                    // Landscape mode (rotated to the right)
                    adjustedSensorData[0] = -sensorData[1]
                    adjustedSensorData[1] = sensorData[0]
                    adjustedSensorData[2] = sensorData[2]
                }
            }
            return adjustedSensorData
        }

        fun Float.roundTo(numFractionDigits: Int): Double {
            val factor = 10.0.pow(numFractionDigits.toDouble())
            return (this * factor).roundToInt() / factor
        }
    }

    enum class TiltDirection {
        LEFT,
        RIGHT,
        BACK,
        FRONT,
        NONE
    }
}