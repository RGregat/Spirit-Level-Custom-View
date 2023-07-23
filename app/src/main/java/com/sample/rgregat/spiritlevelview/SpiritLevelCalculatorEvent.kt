package com.sample.rgregat.spiritlevelview

sealed interface SpiritLevelCalculatorEvent {
    data class UpdateData(
        val pitch: Double,
        val roll: Double,
        val displayOrientation:
        Int, val flatOnGround: Boolean): SpiritLevelCalculatorEvent
}