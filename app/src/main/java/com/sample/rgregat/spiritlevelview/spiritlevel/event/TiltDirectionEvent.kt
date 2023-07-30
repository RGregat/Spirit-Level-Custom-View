package com.sample.rgregat.spiritlevelview.spiritlevel.event

sealed interface TiltDirectionEvent {
    data class TiltDirectionUpdated(
        val newTiltDirection: Int
    ) : TiltDirectionEvent
}
