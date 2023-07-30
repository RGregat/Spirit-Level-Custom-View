package com.sample.rgregat.spiritlevelview.spiritlevel.event

sealed interface SpiritLevelSensorDataEvent {
    data class UpdateData(
        val pitch: Double,
        val roll: Double,
        val displayOrientation:
        Int, val flatOnGround: Boolean): SpiritLevelSensorDataEvent
}