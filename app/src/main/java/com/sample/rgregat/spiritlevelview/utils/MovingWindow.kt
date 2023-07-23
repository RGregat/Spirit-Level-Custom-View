package com.sample.rgregat.spiritlevelview.utils

class MovingWindow(
    private val maxListCount: Int
) {
    private val movingList: MutableList<Float> = mutableListOf()

    fun addValue(value: Float) {
        if (movingList.size > maxListCount) {
            movingList.removeAt(0)
        }
        movingList.add(value)
    }

    fun getMovingList(): MutableList<Float> {
        return movingList
    }

    fun clear() {
        movingList.clear()
    }

    fun getAverage(): Float {
        var sum: Float = 0.0f

        if (movingList.size > 0) {
            for(i in movingList.indices) {
                sum += movingList[i]
            }
        }

        return sum / movingList.size
    }

}