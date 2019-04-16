package com.rocket.testtask

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class PixelImage : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    private val fillPaint = Paint()
    private val drawPaint = Paint()

    private val pathTriangle = Path()
    private val pathRhombus = Path()
    private val pathSquare = Path()

    private var cachedBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cachedBitmap == null) {
            fillPaint.style = Paint.Style.FILL
            fillPaint.color = Color.WHITE
            canvas.drawPaint(fillPaint)

            drawPaint.style = Paint.Style.FILL
            drawPaint.color = Color.BLACK

            drawTriangle(canvas, drawPaint, 100, 100, 100)
            drawRhombus(canvas, drawPaint, 300, 100, 100)
            drawSquare(canvas, drawPaint, 500, 100, 100)

        } else {
            canvas.drawBitmap(cachedBitmap!!, 0F, 0F, null)
        }
    }

    private fun drawTriangle(canvas: Canvas, paint: Paint, x: Int, y: Int, width: Int) {
        val halfWidth: Float = width.toFloat() / 2

        pathTriangle.moveTo(x.toFloat(), y - halfWidth)
        pathTriangle.lineTo(x - halfWidth, y + halfWidth)
        pathTriangle.lineTo(x + halfWidth, y + halfWidth)
        pathTriangle.lineTo(x.toFloat(), y - halfWidth)
        pathTriangle.close()

        canvas.drawPath(pathTriangle, paint)
    }

    private fun drawRhombus(canvas: Canvas, paint: Paint, x: Int, y: Int, width: Int) {
        val halfWidth = width / 2

        pathRhombus.moveTo(x.toFloat(), (y + halfWidth).toFloat())
        pathRhombus.lineTo((x - halfWidth).toFloat(), y.toFloat())
        pathRhombus.lineTo(x.toFloat(), (y - halfWidth).toFloat())
        pathRhombus.lineTo((x + halfWidth).toFloat(), y.toFloat())
        pathRhombus.lineTo(x.toFloat(), (y + halfWidth).toFloat())
        pathRhombus.close()

        canvas.drawPath(pathRhombus, paint)
    }

    private fun drawSquare(canvas: Canvas, paint: Paint, x: Int, y: Int, width: Int) {
        val halfWidth = width / 2

        pathSquare.moveTo((x - halfWidth).toFloat(), (y - halfWidth).toFloat())
        pathSquare.lineTo((x - halfWidth).toFloat(), y.toFloat() + halfWidth)
        pathSquare.lineTo(x.toFloat() + halfWidth, y.toFloat() + halfWidth)
        pathSquare.lineTo(x.toFloat() + halfWidth, (y - halfWidth).toFloat())
        pathSquare.lineTo((x - halfWidth).toFloat(), (y - halfWidth).toFloat())
        pathSquare.close()

        canvas.drawPath(pathSquare, paint)
    }

    fun setSize(newHeight: Int, newWidth: Int) {
        this.layoutParams.width = newWidth
        this.layoutParams.height = newHeight
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {

            cachedBitmap = loadBitmap()

            val pixel = cachedBitmap?.getPixel(event.x.toInt(), event.y.toInt())
            if (pixel == Color.BLACK)
                reDrawRecursive(FractalNode(event.x.toInt(), event.y.toInt()), true)
        }
        return true
    }

    private fun reDrawRecursive(current: FractalNode, directionX: Boolean): FractalNode {
        cachedBitmap?.setPixel(current.x, current.y, Color.WHITE)
        invalidate()

        if (directionX) {
            if (cachedBitmap?.getPixel(current.x + 1, current.y) == Color.BLACK)
                current.nextRight = reDrawRecursive(FractalNode(current.x + 1, current.y), !directionX)
            if (cachedBitmap?.getPixel(current.x - 1, current.y) == Color.BLACK)
                current.nextLeft = reDrawRecursive(FractalNode(current.x - 1, current.y), !directionX)
        } else {
            if (cachedBitmap?.getPixel(current.x, current.y + 1) == Color.BLACK)
                current.nextRight = reDrawRecursive(FractalNode(current.x, current.y + 1), !directionX)
            if (cachedBitmap?.getPixel(current.x, current.y - 1) == Color.BLACK)
                current.nextLeft = reDrawRecursive(FractalNode(current.x - 1, current.y - 1), !directionX)
        }
        return current
    }

    private fun loadBitmap(): Bitmap {
        val b = Bitmap.createBitmap(this.layoutParams.width, this.layoutParams.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        this.layout(this.left, this.top, this.right, this.bottom)
        this.draw(c)
        return b
    }
}
