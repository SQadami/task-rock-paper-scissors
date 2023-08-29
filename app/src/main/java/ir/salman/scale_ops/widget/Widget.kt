package ir.salman.scale_ops.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import ir.salman.scale_ops.utils.dpToPx

@Suppress("UNUSED_PARAMETER")
class Widget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context) {

    lateinit var type: Type

    private val background by lazy {
        GradientDrawable().apply {
            cornerRadius = dpToPx(CORNER_RADIUS).toFloat()
            setColor(type.color)
        }
    }

    private val textPaint by lazy {
        Paint().apply {
            color = Color.WHITE
            textSize = dpToPx(TEXT_SIZE).toFloat()
            textAlign = Paint.Align.CENTER
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureDimension(SIZE, widthMeasureSpec),
            measureDimension(SIZE, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return
        setBackground(background)

        val xPos = width / 2
        val yPos = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()
        canvas.drawText(type.char, xPos.toFloat(), yPos.toFloat(), textPaint)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("Widget", "The view is too small, the content might get cut")
        }
        return result
    }

    companion object {

        private const val TEXT_SIZE = 14
        private const val CORNER_RADIUS = 4

        val SIZE = dpToPx(24)
    }

    enum class Type(
        val color: Int,
        val char: String,
    ) {
        ROCK(Color.RED, "R"),
        SCISSOR(Color.BLUE, "S"),
        PAPER(Color.GREEN, "P"),
    }
}