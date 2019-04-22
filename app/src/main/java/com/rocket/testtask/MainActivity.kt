package com.rocket.testtask

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var alSpeed = 0
    private var currentImage: PixelImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btGenerate.setOnClickListener {
            val width = checkMaxWidth(checkInput(etWidth.text.toString()))
            val height = checkMaxHeight(checkInput(etHeight.text.toString()))
            generate(width, height)
        }

        speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                alSpeed = seekBar?.progress ?: 0
                currentImage?.setSpeed(seekBar?.progress ?: 0)
            }
        })
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
        pixelImage.init(height, width)
        currentImage = pixelImage
        currentImage?.setSpeed(alSpeed)
    }

    private fun checkInput(numberText: String): Int {
        return try {
            Integer.parseInt(numberText)
        } catch (e: NumberFormatException) {
            0
        }
    }
}
