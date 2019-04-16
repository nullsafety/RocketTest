package com.rocket.testtask

class FractalNode(
    val x: Int,
    val y: Int
) {
    var nextLeft: FractalNode? = null
    var nextRight: FractalNode? = null
}