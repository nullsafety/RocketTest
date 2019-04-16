package com.rocket.testtask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btGenerate.setOnClickListener {
            generate(checkInput(etWidth.text.toString()), checkInput(etHeight.text.toString()))
        }
    }

    private fun generate(width: Int, height: Int) {
        pixelImage.setSize(height, width)
    }

    private fun checkInput(numberText: String): Int {
        return try {
            Integer.parseInt(numberText)
        } catch (e: NumberFormatException) {
            0
        }
    }
}
