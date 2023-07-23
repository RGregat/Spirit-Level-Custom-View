package com.sample.rgregat.spiritlevelview.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.view.animation.LinearInterpolator
import com.sample.rgregat.spiritlevelview.R
import com.sample.rgregat.spiritlevelview.utils.Utils
import kotlin.math.cos
import kotlin.math.sin

class SpiritLevelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val OUTER_CIRCLE_REDUCTION_FACTOR = 0.8f
        const val UPDATE_DELAY = 150L
        const val DEFAULT_BUBBLE_COLOR = Color.GREEN
        const val DEFAULT_THRESHOLD_COLOR = Color.RED
        const val DEFAULT_OUTER_CIRCLE_COLOR = Color.BLACK
        const val DEFAULT_INNER_CIRCLE_COLOR = Color.BLACK
        const val DEFAULT_CROSS_COLOR = Color.BLACK
        const val DEFAULT_BUBBLE_SIZE = 25f
        const val DEFAULT_OUTER_CIRCLE_STROKE_WIDTH = 2.5f
        const val DEFAULT_INNER_CIRCLE_STROKE_WIDTH = 2.5f
        const val DEFAULT_CROSS_STROKE_WIDTH = 2.5f
        const val DEFAULT_BUBBLE_INTERPOLATION_TIMER = 150L
        const val DEFAULT_WITH_THRESHOLD_INDICATION = true
        const val DEFAULT_THRESHOLD_VALUE = 5f
        const val DEFAULT_WITH_LABEL = true
        const val DEFAULT_LABEL_TEXT_SIZE = 20f // Optimize to use SP

        fun builder(context: Context): Builder {
            return Builder(context)
        }
    }

    private var bubbleColor: Int
    private var bubbleThresholdColor: Int
    private var outerCircleStrokeColor: Int
    private var innerCircleStrokeColor: Int
    private var crossStrokeColor: Int
    private var bubbleSize: Float
    private var outerCircleStrokeWidth: Float
    private var innerCircleStrokeWidth: Float
    private var crossStrokeWidth: Float
    private var bubbleInterpolationTimer: Long
    private var withThresholdIndication: Boolean
    private var thresholdValue: Float
    private var withLabel: Boolean
    private var labelTextSize: Float

    private val crossPaint: Paint = Paint()
    private val outerCirclePaint: Paint = Paint()
    private val innerCirclePaint: Paint = Paint()
    private val bubblePaint: Paint = Paint()
    private val textPaint: TextPaint = TextPaint()

    private var animatorX: ValueAnimator? = null
    private var animatorY: ValueAnimator? = null

    private var cX = 0f
    private var cY = 0f
    private var outerCircleRadius = 0f

    private var bubbleX = 0f
    private var bubbleY = 0f

    private var bubbleXOld = 0f
    private var bubbleYOld = 0f

    private var bubbleXNew = 0f
    private var bubbleYNew = 0f

    private var pitch = 0.0
    private var roll = 0.0

    private var innerCircleRadius = 0.0f

    private var pitchText: String = ""
    private var rollText: String = ""

    private var pitchTextBound: Rect = Rect()
    private var rollTextBound: Rect = Rect()

    private var pitchTextX: Float = 0f
    private var pitchTextY: Float = 0f
    private var rollTextX: Float = 0f
    private var rollTextY: Float = 0f

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SpiritLevelView,
            0,
            0
        ).apply {
            try {
                bubbleColor = getColor(R.styleable.SpiritLevelView_bubbleColor, DEFAULT_BUBBLE_COLOR)
                bubbleThresholdColor = getColor(R.styleable.SpiritLevelView_bubbleThresholdColor, DEFAULT_THRESHOLD_COLOR)
                outerCircleStrokeColor = getColor(R.styleable.SpiritLevelView_outerCircleStrokeColor, DEFAULT_OUTER_CIRCLE_COLOR)
                innerCircleStrokeColor = getColor(R.styleable.SpiritLevelView_innerCircleStrokeColor, DEFAULT_INNER_CIRCLE_COLOR)
                crossStrokeColor = getColor(R.styleable.SpiritLevelView_crossStrokeColor, DEFAULT_CROSS_COLOR)

                bubbleSize = getFloat(R.styleable.SpiritLevelView_bubbleSize, DEFAULT_BUBBLE_SIZE)
                outerCircleStrokeWidth = getFloat(R.styleable.SpiritLevelView_outerCircleStrokeWidth, DEFAULT_OUTER_CIRCLE_STROKE_WIDTH)
                innerCircleStrokeWidth = getFloat(R.styleable.SpiritLevelView_innerCircleStrokeWidth, DEFAULT_INNER_CIRCLE_STROKE_WIDTH)
                crossStrokeWidth = getFloat(R.styleable.SpiritLevelView_crossStrokeWidth, DEFAULT_CROSS_STROKE_WIDTH)

                bubbleInterpolationTimer = getInt(R.styleable.SpiritLevelView_bubbleInterpolationTimer, DEFAULT_BUBBLE_INTERPOLATION_TIMER.toInt()).toLong()

                withThresholdIndication = getBoolean(R.styleable.SpiritLevelView_withThresholdIndication, DEFAULT_WITH_THRESHOLD_INDICATION)
                thresholdValue = getFloat(R.styleable.SpiritLevelView_thresholdValue, DEFAULT_THRESHOLD_VALUE)

                withLabel = getBoolean(R.styleable.SpiritLevelView_withLabel, DEFAULT_WITH_LABEL)
                labelTextSize = getFloat(R.styleable.SpiritLevelView_labelTextSize, DEFAULT_LABEL_TEXT_SIZE)
            } finally {
                recycle()
            }
        }
    }

    init {
        outerCirclePaint.isAntiAlias = true
        outerCirclePaint.style = Paint.Style.STROKE
        outerCirclePaint.color = outerCircleStrokeColor
        outerCirclePaint.strokeWidth = outerCircleStrokeWidth

        innerCirclePaint.isAntiAlias = true
        innerCirclePaint.style = Paint.Style.STROKE
        innerCirclePaint.color = innerCircleStrokeColor
        innerCirclePaint.strokeWidth = innerCircleStrokeWidth

        crossPaint.isAntiAlias = true
        crossPaint.style = Paint.Style.STROKE
        crossPaint.color = crossStrokeColor
        crossPaint.strokeWidth = crossStrokeWidth

        bubblePaint.isAntiAlias = true
        bubblePaint.style = Paint.Style.FILL
        bubblePaint.color = bubbleColor

        textPaint.isAntiAlias = true
        textPaint.color = Color.BLACK
        textPaint.textSize = labelTextSize
    }

    constructor(
        context: Context,
        bubbleColor: Int,
        bubbleThresholdColor: Int,
        outerCircleStrokeColor: Int,
        innerCircleStrokeColor: Int,
        crossStrokeColor: Int,
        bubbleSize: Float,
        outerCircleStrokeWidth: Float,
        innerCircleStrokeWidth: Float,
        crossStrokeWidth: Float,
        bubbleInterpolationTimer: Long,
        withThresholdIndication: Boolean,
        thresholdValue: Float,
        withLabel: Boolean,
        labelTextSize: Float
    ) : this(context) {
        this.bubbleColor = bubbleColor
        this.bubbleThresholdColor = bubbleThresholdColor
        this.outerCircleStrokeColor = outerCircleStrokeColor
        this.innerCircleStrokeColor = innerCircleStrokeColor
        this.crossStrokeColor = crossStrokeColor
        this.bubbleSize = bubbleSize
        this.outerCircleStrokeWidth = outerCircleStrokeWidth
        this.innerCircleStrokeWidth = innerCircleStrokeWidth
        this.crossStrokeWidth = crossStrokeWidth
        this.bubbleInterpolationTimer = bubbleInterpolationTimer
        this.withThresholdIndication = withThresholdIndication
        this.thresholdValue = thresholdValue
        this.withLabel = withLabel
        this.labelTextSize = labelTextSize
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        cX = w * 0.5f
        cY = h * 0.5f
        bubbleX = cX
        bubbleY = cY

        outerCircleRadius = if (cX > cY) cY else cX
        outerCircleRadius *= OUTER_CIRCLE_REDUCTION_FACTOR
        innerCircleRadius = ((outerCircleRadius / 90) * thresholdValue) + 25
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) {
            return
        }

        // Draw the crosshair
        canvas.drawLine(cX, cY - (outerCircleRadius), cX, cY + (outerCircleRadius), crossPaint)
        canvas.drawLine(cX - (outerCircleRadius), cY, cX + (outerCircleRadius), cY, crossPaint)

        // Draw the outer circle (boundary)
        canvas.drawCircle(cX, cY, outerCircleRadius, outerCirclePaint)

        // Draw the inner circle (threshold)
        canvas.drawCircle(cX, cY, innerCircleRadius, innerCirclePaint)

        // Draw the spirit-level (bubble) based on the current values
        // for pitch and roll
        canvas.drawCircle(bubbleX, bubbleY, bubbleSize, bubblePaint)

        // Draw pitch and roll values as text labels
        if(withLabel) {
            canvas.drawText(
                pitchText,
                pitchTextX,
                pitchTextY,
                textPaint
            )
            canvas.drawText(
                rollText,
                rollTextX,
                rollTextY,
                textPaint
            )
        }
    }

    fun updateData(
        pitch: Double,
        roll: Double,
        rotation: Int,
        flatOnGround: Boolean
    ) {
        if (this.pitch.toInt() == pitch.toInt() &&
            this.roll.toInt() == roll.toInt()
        ) {
            return
        }

        this.pitch = pitch
        this.roll = roll

        if (withThresholdIndication) {
            val tiltDirection = checkAgainstThreshold(
                flatOnGround,
                pitch,
                roll
            )
            chooseBubbleColor(tiltDirection)
        }

        adjustPitchValue(rotation, flatOnGround)
        calculateBubblePosition()
        animateBubblePosition()

        if (withLabel) {
            createTextElements()
        }
    }

    private fun adjustPitchValue(rotation: Int, flatOnGround: Boolean) {
        when (rotation) {
            Surface.ROTATION_0 -> {
                if (!flatOnGround) {
                    this.pitch -= 90.0
                }
            }

            Surface.ROTATION_90 -> {
                if (!flatOnGround) {
                    this.pitch += 90.0
                }
            }

            Surface.ROTATION_180 -> {

            }

            Surface.ROTATION_270 -> {
                if (!flatOnGround) {
                    this.pitch += 90.0
                }
            }
        }
    }

    private fun checkAgainstThreshold(
        flatOnGround: Boolean,
        pitch: Double,
        roll: Double
    ): Utils.TiltDirection {
        var tiltDirection: Utils.TiltDirection = Utils.TiltDirection.NONE

        when (display.rotation) {
            Surface.ROTATION_0 -> {
                if (!flatOnGround) {
                    if (pitch < 90 - thresholdValue) {
                        tiltDirection = Utils.TiltDirection.BACK
                    } else if (pitch > 90 + thresholdValue) {
                        tiltDirection = Utils.TiltDirection.FRONT
                    } else if (roll < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.LEFT
                    } else if (roll > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.RIGHT
                    }
                } else {
                    if (pitch < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.BACK
                    } else if (pitch > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.FRONT
                    } else if (roll < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.LEFT
                    } else if (roll > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.RIGHT
                    }
                }
            }

            Surface.ROTATION_90 -> {
                if (flatOnGround) {
                    if (pitch < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.FRONT
                    } else if (pitch > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.BACK
                    } else if (roll < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.LEFT
                    } else if (roll > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.RIGHT
                    }
                } else {
                    if (pitch < -(90 + thresholdValue)) {
                        tiltDirection = Utils.TiltDirection.FRONT
                    } else if (pitch > -(90 - thresholdValue)) {
                        tiltDirection = Utils.TiltDirection.BACK
                    } else if (roll < -thresholdValue) {
                        tiltDirection = Utils.TiltDirection.LEFT
                    } else if (roll > thresholdValue) {
                        tiltDirection = Utils.TiltDirection.RIGHT
                    }
                }
            }

            Surface.ROTATION_180 -> {}

            Surface.ROTATION_270 -> {}
        }

        return tiltDirection
    }

    private fun chooseBubbleColor(tiltDirection: Utils.TiltDirection) {
        when (tiltDirection) {
            Utils.TiltDirection.NONE -> {
                bubblePaint.color = bubbleColor
            }

            Utils.TiltDirection.BACK -> {
                bubblePaint.color = bubbleThresholdColor
            }

            Utils.TiltDirection.FRONT -> {
                bubblePaint.color = bubbleThresholdColor
            }

            Utils.TiltDirection.LEFT -> {
                bubblePaint.color = bubbleThresholdColor
            }

            Utils.TiltDirection.RIGHT -> {
                bubblePaint.color = bubbleThresholdColor
            }
        }
    }

    private fun calculateBubblePosition() {
        bubbleXNew =
            (cX + (outerCircleRadius - bubbleSize / 2) * sin(Math.toRadians(this.roll)) * cos(
                Math.toRadians(
                    this.pitch
                )
            )).toFloat()
        bubbleYNew =
            (cY - (outerCircleRadius - bubbleSize / 2) * sin(Math.toRadians(this.pitch))).toFloat()
    }

    private fun animateBubblePosition() {
        if (animatorX != null) {
            animatorX!!.cancel()
        }

        animatorX = ValueAnimator.ofFloat(bubbleXOld, bubbleXNew).apply {
            interpolator = LinearInterpolator()
            duration = bubbleInterpolationTimer
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                bubbleX = animatedValue

                // I'm lazy here, the default anchor position of a drawn
                // text is at the top left corner. To support all possible
                // anchor positions, this logic here has to adopt to it.
                if ((bubbleX + pitchTextBound.right) >= (cX + outerCircleRadius)) {
                    pitchTextX = bubbleX - bubbleSize - pitchTextBound.width()
                    rollTextX = bubbleX - bubbleSize - rollTextBound.width()
                } else {
                    pitchTextX = bubbleX + bubbleSize
                    rollTextX = bubbleX + bubbleSize
                }

                invalidate()
            }
        }
        animatorX!!.start()

        if (animatorY != null) {
            animatorY!!.cancel()
        }

        animatorY = ValueAnimator.ofFloat(bubbleYOld, bubbleYNew).apply {
            interpolator = LinearInterpolator()
            duration = bubbleInterpolationTimer
            addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                bubbleY = animatedValue

                // I'm lazy here, the default anchor position of a drawn
                // text is at the top left corner. To support all possible
                // anchor positions, this logic here has to adopt to it.
                if ((bubbleY + rollTextBound.bottom) >= (cY + outerCircleRadius)) {
                    pitchTextY = bubbleY - bubbleSize - pitchTextBound.height() - 5
                    rollTextY = bubbleY - bubbleSize
                } else {
                    pitchTextY = bubbleY + bubbleSize
                    rollTextY = bubbleY + bubbleSize + pitchTextBound.height() + 5
                }

                invalidate()
            }
        }
        animatorY!!.start()

        bubbleXOld = bubbleXNew
        bubbleYOld = bubbleYNew
    }

    private fun createTextElements() {
        pitchText =  "Pitch: %.2f°".format(pitch)
        rollText = "Roll: %.2f°".format(roll)

        textPaint.getTextBounds(pitchText, 0, pitchText.length, pitchTextBound)
        textPaint.getTextBounds(rollText, 0, rollText.length, rollTextBound)
    }

    class Builder(private val context: Context) {
        private var bubbleColor: Int = DEFAULT_BUBBLE_COLOR
        private var bubbleThresholdColor: Int = DEFAULT_THRESHOLD_COLOR
        private var outerCircleStrokeColor: Int = DEFAULT_OUTER_CIRCLE_COLOR
        private var innerCircleStrokeColor: Int = DEFAULT_INNER_CIRCLE_COLOR
        private var crossStrokeColor: Int = DEFAULT_CROSS_COLOR
        private var bubbleSize: Float = DEFAULT_BUBBLE_SIZE
        private var outerCircleStrokeWidth: Float = DEFAULT_OUTER_CIRCLE_STROKE_WIDTH
        private var innerCircleStrokeWidth: Float = DEFAULT_INNER_CIRCLE_STROKE_WIDTH
        private var crossStrokeWidth: Float = DEFAULT_CROSS_STROKE_WIDTH
        private var bubbleInterpolationTimer: Long = DEFAULT_BUBBLE_INTERPOLATION_TIMER
        private var withThresholdIndication: Boolean = DEFAULT_WITH_THRESHOLD_INDICATION
        private var thresholdValue: Float = DEFAULT_THRESHOLD_VALUE
        private var withLabel: Boolean = DEFAULT_WITH_LABEL
        private var labelTextSize: Float = DEFAULT_LABEL_TEXT_SIZE

        fun bubbleColor(color: Int) = apply { bubbleColor = color }
        fun bubbleThresholdColor(color: Int) = apply { bubbleThresholdColor = color }
        fun outerCircleStrokeColor(color: Int) = apply { outerCircleStrokeColor = color }
        fun innerCircleStrokeColor(color: Int) = apply { innerCircleStrokeColor = color }
        fun crossStrokeColor(color: Int) = apply { crossStrokeColor = color }
        fun bubbleSize(size: Float) = apply { bubbleSize = size }
        fun outerCircleStrokeWidth(width: Float) = apply { outerCircleStrokeWidth = width }
        fun innerCircleStrokeWidth(width: Float) = apply { innerCircleStrokeWidth = width }
        fun crossStrokeWidth(width: Float) = apply { crossStrokeWidth = width }
        fun bubbleInterpolationTimer(timer: Long) = apply { bubbleInterpolationTimer = timer }
        fun withThresholdIndication(withThreshold: Boolean) = apply { withThresholdIndication = withThreshold }
        fun thresholdValue(value: Float) = apply { thresholdValue = value }
        fun withLabel(value: Boolean) = apply { withLabel = value }
        fun labelTextSize(value: Float) = apply { labelTextSize = value}

        fun build(): SpiritLevelView {
            return SpiritLevelView(
                context,
                bubbleColor,
                bubbleThresholdColor,
                outerCircleStrokeColor,
                innerCircleStrokeColor,
                crossStrokeColor,
                bubbleSize,
                outerCircleStrokeWidth,
                innerCircleStrokeWidth,
                crossStrokeWidth,
                bubbleInterpolationTimer,
                withThresholdIndication,
                thresholdValue,
                withLabel,
                labelTextSize
            )
        }
    }
}