package com.sample.rgregat.spiritlevelview.spiritlevel.utils

import androidx.annotation.IntDef
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelTiltDirectionType.Companion.BACK
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelTiltDirectionType.Companion.FRONT
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelTiltDirectionType.Companion.LEFT
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelTiltDirectionType.Companion.NONE
import com.sample.rgregat.spiritlevelview.spiritlevel.utils.SpiritLevelTiltDirectionType.Companion.RIGHT

@IntDef(NONE, LEFT, RIGHT, BACK, FRONT)
@Retention(AnnotationRetention.SOURCE)
annotation class SpiritLevelTiltDirectionType {
    companion object {
        const val NONE = 0
        const val LEFT = 1
        const val RIGHT = 2
        const val BACK = 3
        const val FRONT = 4
    }

}
