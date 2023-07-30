package com.sample.rgregat.spiritlevelview

import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import com.sample.rgregat.spiritlevelview.databinding.ActivityMainBinding
import com.sample.rgregat.spiritlevelview.spiritlevel.event.SpiritLevelSensorDataEvent
import com.sample.rgregat.spiritlevelview.spiritlevel.event.TiltDirectionEvent
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelSensorData
import com.sample.rgregat.spiritlevelview.spiritlevel.view.SpiritLevelView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var spiritLevelSensorData: SpiritLevelSensorData
    private lateinit var spiritLevelView: SpiritLevelView
    private var display_: Display? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding
            .inflate(
                layoutInflater,
                null,
                false
            )

        setContentView(binding.root)

        // Get from the first SpiritLevelView information about the TiltDirection
        binding
            .spiritLevelView
            .registerTiltDirectionEven(this::onTiltDirectionEvent)

        // Create a second SpiritLevelView with the Builder class.
        // Configure the available attributes through the Builder class
        // or keep the default attributes.
        spiritLevelView = SpiritLevelView.Builder(this)
            .viewBackgroundColor(getColor(R.color.white))
            .spiritLevelBackgroundColor(getColor(R.color.bluegray800))
            .outerCircleStrokeColor(getColor(R.color.bluegray400))
            .innerCircleStrokeColor(getColor(R.color.bluegray100))
            .crossStrokeColor(getColor(R.color.bluegray100))
            .bubbleColor(getColor(R.color.blue500))
            .bubbleThresholdColor(getColor(R.color.pink500))
            .labelColor(getColor(R.color.white))
            .outerCircleStrokeWidth(10f)
            .innerCircleStrokeWidth(2.5f)
            .crossStrokeWidth(2.5f)
            .bubbleSize(10f)
            .withLabel(true)
            .labelTextSize(30f)
            .bubbleInterpolationTimer(150L)
            .withThresholdIndication(true)
            .thresholdValue(5f)
            .withFlatOnGroundCorrection(false)
            .build()
        binding.spiritLevelViewContainer.addView(spiritLevelView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        display_ = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        spiritLevelSensorData = SpiritLevelSensorData(
            display_!!,
            sensorManager,
            this::onEvent)

        lifecycle.addObserver(spiritLevelSensorData)
    }

    private fun onTiltDirectionEvent(event: TiltDirectionEvent) {
        when (event) {
            is TiltDirectionEvent.TiltDirectionUpdated -> {
                Log.d("MainActivity", "New TiltDirection: ${event.newTiltDirection}")
            }
        }
    }

    private fun onEvent(event: SpiritLevelSensorDataEvent) {
        when(event) {
            is SpiritLevelSensorDataEvent.UpdateData -> {
                binding
                    .spiritLevelView
                    .updateData(
                        event.pitch,
                        event.roll,
                        event.displayOrientation,
                        event.flatOnGround)

                spiritLevelView.updateData(
                    event.pitch,
                    event.roll,
                    event.displayOrientation,
                    event.flatOnGround)
            }

            else -> {}
        }
    }
}