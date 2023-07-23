package com.sample.rgregat.spiritlevelview

import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import com.sample.rgregat.spiritlevelview.databinding.ActivityMainBinding
import com.sample.rgregat.spiritlevelview.utils.SpiritLevelCalculator
import com.sample.rgregat.spiritlevelview.view.SpiritLevelView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var spiritLevelCalculator: SpiritLevelCalculator
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

        spiritLevelView = SpiritLevelView.Builder(this)
            .bubbleColor(getColor(R.color.blue500))
            .bubbleThresholdColor(getColor(R.color.pink500))
            .bubbleSize(10f)
            .withLabel(false)
            .build()
        binding.spiritLevelViewContainer.addView(spiritLevelView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        display_ = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        spiritLevelCalculator = SpiritLevelCalculator(
            display_!!,
            sensorManager,
            this::onEvent)

        lifecycle.addObserver(spiritLevelCalculator)
    }

    private fun onEvent(event: SpiritLevelCalculatorEvent) {
        when(event) {
            is SpiritLevelCalculatorEvent.UpdateData -> {
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
        }
    }
}