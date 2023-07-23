package com.sample.rgregat.spiritlevelview.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.view.Display
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sample.rgregat.spiritlevelview.SpiritLevelCalculatorEvent
import com.sample.rgregat.spiritlevelview.utils.Utils.Companion.roundTo
import com.sample.rgregat.spiritlevelview.view.SpiritLevelView.Companion.UPDATE_DELAY
import kotlin.math.abs

class SpiritLevelCalculator(
    private val display: Display,
    private val sensorManager: SensorManager,
    private val onEvent: (SpiritLevelCalculatorEvent) -> Unit
) : SensorEventListener, DefaultLifecycleObserver {

    private val handler = Handler(Looper.getMainLooper())
    private val updateOutputRunnable: Runnable = Runnable { updateOutput() }

    private var lastGyroTimestamp: Long = 0
    private var displayAdjustedAccelValues: FloatArray = FloatArray(3)

    private val kalmanFilter: KalmanFilter = KalmanFilter()

    // Collect the n latest values
    private var movingWindowPitch: MovingWindow = MovingWindow(10)
    private var movingWindowRoll: MovingWindow = MovingWindow(10)

    private var tiltDirection = Utils.TiltDirection.NONE

    override fun onResume(owner: LifecycleOwner) {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also { gyroscope ->
            sensorManager.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        handler.postDelayed(updateOutputRunnable, UPDATE_DELAY)
    }

    override fun onPause(owner: LifecycleOwner) {
        handler.removeCallbacks(updateOutputRunnable)

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            // Read the raw values from the accelerometer sensor
            val accelValues = floatArrayOf(event.values[0], event.values[1], event.values[2])

            // Adjust the axis of the accelerometer sensor data based on the display orientation.
            displayAdjustedAccelValues = Utils.adjustToDisplayRotation(display, accelValues)

            // Let the Kalman-Filter do it's magical thing with the
            // sensor reading
            kalmanFilter.processAccelData(displayAdjustedAccelValues)

        } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Determine the exact time when the last time sensor data from the
            // Gyroscope arrived here
            val currentTimestamp = event.timestamp
            val dt =
                ((currentTimestamp - lastGyroTimestamp) / 1_000_000_000f).toLong() // Conversion in seconds
            lastGyroTimestamp = currentTimestamp

            // Read the raw values from the gyroscope sensor
            val gyroValues = floatArrayOf(event.values[0], event.values[1], event.values[2])

            // Adjust the axis of the gyroscope sensor data based on the display orientation.
            val displayAdjustedGyroValues = Utils.adjustToDisplayRotation(display, gyroValues)

            // Let the Kalman-Filter do it's magical thing with the
            // sensor reading
            kalmanFilter.processGyroData(displayAdjustedGyroValues, dt)

            // Get the calculated Values for pitch and roll
            val orientation = kalmanFilter.getOrientation()
            val pitch = Utils.toDegree(orientation[0]).toFloat()
            val roll = Utils.toDegree(orientation[2]).toFloat()

            // Feed pitch and roll into the corresponding MovingWindow instance.
            movingWindowPitch.addValue(pitch)
            movingWindowRoll.addValue(roll)

        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun updateOutput() {
        tiltDirection = Utils.TiltDirection.NONE

        var flatOnGround = false

        if ((abs(displayAdjustedAccelValues[2]) > abs(displayAdjustedAccelValues[0])) &&
            (abs(displayAdjustedAccelValues[2]) > abs(displayAdjustedAccelValues[1]))
        ) {
            flatOnGround = true
        }

        val avgPitch = movingWindowPitch.getAverage()
        val avgRoll = movingWindowRoll.getAverage()

        val roundedPitch = avgPitch.roundTo(1)
        val roundedRoll = avgRoll.roundTo(1)

        onEvent(SpiritLevelCalculatorEvent.UpdateData( roundedPitch,
            roundedRoll,
            display.rotation,
            flatOnGround))

        handler.postDelayed(updateOutputRunnable, UPDATE_DELAY)
    }
}