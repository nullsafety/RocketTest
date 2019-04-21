package com.rocket.testtask

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.concurrent.Executors
import kotlin.random.Random


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

    private var visualization = Visualization.Queue

    private var cachedBitmap: Bitmap? = null

    private var canvas: Canvas? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cachedBitmap == null) {
            generate(canvas)
        } else {
            canvas.drawBitmap(cachedBitmap!!, 0F, 0F, null)
        }
    }

    private fun generate(canvas: Canvas) {
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = Color.WHITE
        canvas.drawPaint(fillPaint)

        drawPaint.style = Paint.Style.FILL
        drawPaint.color = Color.BLACK

        drawTriangle(canvas, drawPaint, 100, 100, 100)
        drawRhombus(canvas, drawPaint, 170, 150, 100)
        drawSquare(canvas, drawPaint, 250, 120, 100)
        drawTriangle(canvas, drawPaint, 170, 200, 100)
        drawSquare(canvas, drawPaint, 250, 270, 100)
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

    private fun setSize(newHeight: Int, newWidth: Int) {
        this.layoutParams.width = newWidth
        this.layoutParams.height = newHeight
        invalidate()
    }

    fun init(newHeight: Int, newWidth: Int, visualization: Visualization) {
        setSize(newHeight, newWidth)
        this.visualization = visualization
        if (canvas != null)
            generate(canvas!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {

            cachedBitmap = loadBitmap()

            val pixel = cachedBitmap?.getPixel(event.x.toInt(), event.y.toInt())
            if (pixel == Color.BLACK) {
                val graph = HashSet<PixelNode>()
                val root = PixelNode(event.x.toInt(), event.y.toInt(), true)
                graph.add(root)

                addNodeRecursive(graph, root)

                when (visualization) {
                    Visualization.Queue -> drawQueue(graph)
                    Visualization.Random -> drawRandom(graph)
                }
            }
        }
        return true
    }

    private val drawExecutor = Executors.newSingleThreadExecutor()
    private fun drawQueue(queue: HashSet<PixelNode>) {
        for (node in queue) {
            drawExecutor.submit {
                Thread.sleep(0, 1)
                cachedBitmap?.setPixel(node.x, node.y, Color.WHITE)
                invalidate()
            }
        }
    }

    private fun drawRandom(graph: HashSet<PixelNode>) {
        val list = graph.toMutableList()
        for (i in 0 until graph.size) {
            drawExecutor.submit {
                Thread.sleep(0, 1)
                val random = Random.nextInt(list.size)
                cachedBitmap?.setPixel(list[random].x, list[random].y, Color.WHITE)
                list.removeAt(random)
                invalidate()
            }
        }
    }

    private fun addNodeRecursive(
        pixels: HashSet<PixelNode>,
        current: PixelNode
    ) {
        if (current.childDirectionX) {
            if (cachedBitmap?.getPixel(current.x + 1, current.y) == Color.BLACK)
                addNode(current, pixels, current.x + 1, current.y) { newNode: PixelNode ->
                    current.nextRight = newNode
                }
            if (cachedBitmap?.getPixel(current.x - 1, current.y) == Color.BLACK)
                addNode(current, pixels, current.x - 1, current.y) { newNode: PixelNode ->
                    current.nextLeft = newNode
                }
        } else {
            if (cachedBitmap?.getPixel(current.x, current.y + 1) == Color.BLACK)
                addNode(current, pixels, current.x, current.y + 1) { newNode: PixelNode ->
                    current.nextRight = newNode
                }
            if (cachedBitmap?.getPixel(current.x, current.y - 1) == Color.BLACK)
                addNode(current, pixels, current.x, current.y - 1) { newNode: PixelNode ->
                    current.nextLeft = newNode
                }
        }
    }

    private fun addNode(
        current: PixelNode,
        pixels: HashSet<PixelNode>,
        x: Int,
        y: Int,
        add: (PixelNode) -> Unit
    ) {
        val node = PixelNode(x, y, !current.childDirectionX)
        if (!pixels.contains(node)) {
            pixels.add(node)
            add.invoke(node)
            addNodeRecursive(pixels, node)
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
