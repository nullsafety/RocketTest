package com.rocket.testtask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.Executors
import kotlin.random.Random


class PixelImage : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    private val drawPaint = Paint()

    private var speed = 0

    private var cachedBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cachedBitmap == null) {
            generate(canvas)
        } else {
            canvas.drawBitmap(cachedBitmap!!, 0F, 0F, null)
        }
    }

    private fun generate(canvas: Canvas) {
        for (x in 0..canvas.width) {
            for (y in 0..canvas.height) {
                if (Random.nextFloat() > .2) {
                    canvas.drawPoint(x.toFloat(), y.toFloat(), drawPaint)
                }
            }
        }
    }

    fun init(newHeight: Int, newWidth: Int) {
        setSize(newHeight, newWidth)
        cachedBitmap = loadBitmap()
    }

    private fun setSize(newHeight: Int, newWidth: Int) {
        this.layoutParams.width = newWidth
        this.layoutParams.height = newHeight
        invalidate()
    }

    fun setSpeed(speed: Int) {
        this.speed = speed * 10
    }

    private val drawExecutor = Executors.newSingleThreadExecutor()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {

            val pixel = cachedBitmap?.getPixel(event.x.toInt(), event.y.toInt())
            if (pixel == Color.BLACK) {
                val root = PixelNode(event.x.toInt(), event.y.toInt(), true)
                val list = ArrayList<PixelNode>()
                runDraw(root, list)

                for (item in list) {
                    cachedBitmap?.setPixel(item.x, item.y, Color.BLACK)
                }

                val localSpeed = 1000 - speed.toLong()
                for (item in list) {
                    drawExecutor.submit {
                        Thread.sleep(localSpeed)
                        cachedBitmap?.setPixel(item.x, item.y, Color.WHITE)
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    private fun runDraw(
        current: PixelNode,
        list: ArrayList<PixelNode>
    ) {
        addNodeRecursive(current, list)
    }

    private fun addNodeRecursive(
        current: PixelNode,
        list: ArrayList<PixelNode>
    ) {

        list.add(current)
        cachedBitmap?.setPixel(current.x, current.y, Color.WHITE)

        if (current.x + 1 < cachedBitmap?.width!!
            && cachedBitmap?.getPixel(current.x + 1, current.y) == Color.BLACK
        ) {
            runDraw(PixelNode(current.x + 1, current.y, !current.childDirectionX), list)
        }

        if (current.x > 0 && cachedBitmap?.getPixel(current.x - 1, current.y) == Color.BLACK) {
            runDraw(PixelNode(current.x - 1, current.y, !current.childDirectionX), list)
        }

        if (current.y + 1 < cachedBitmap?.height!!
            && cachedBitmap?.getPixel(current.x, current.y + 1) == Color.BLACK
        ) {
            runDraw(PixelNode(current.x, current.y + 1, !current.childDirectionX), list)
        }

        if (current.y > 0 && cachedBitmap?.getPixel(current.x, current.y - 1) == Color.BLACK) {
            runDraw(PixelNode(current.x, current.y - 1, !current.childDirectionX), list)
        }
    }

    private fun loadBitmap(): Bitmap {
        val b = Bitmap.createBitmap(this.layoutParams.width, this.layoutParams.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        this.layout(this.left, this.top, this.right, this.bottom)
        this.draw(c)
        return b
    }
}
