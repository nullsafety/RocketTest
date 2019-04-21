package com.rocket.testtask

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var selected = Visualization.Queue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                queue.id -> selected = Visualization.Queue
                random.id -> selected = Visualization.Random
            }
        }

        btGenerate.setOnClickListener {
            val width = checkMaxWidth(checkInput(etWidth.text.toString()))
            val height = checkMaxHeight(checkInput(etHeight.text.toString()))
            generate(width, height)
        }
    }

    private fun checkMaxHeight(input: Int): Int {
        return if (input > pixelImageContainer.width)
            return pixelImageContainer.width
        else input
    }

    private fun checkMaxWidth(input: Int): Int {
        return if (input > pixelImageContainer.width)
            return pixelImageContainer.width
        else input
    }

    private fun generate(width: Int, height: Int) {
        pixelImageContainer.removeAllViews()

        val params =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
        params.gravity = Gravity.CENTER

        val pixelImage = PixelImage(this)
        pixelImage.layoutParams = params
        pixelImageContainer.addView(pixelImage)
        pixelImage.init(height, width, selected)
    }

    private fun checkInput(numberText: String): Int {
        return try {
            Integer.parseInt(numberText)
        } catch (e: NumberFormatException) {
            0
        }
    }
}
