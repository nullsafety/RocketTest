package com.rocket.testtask

data class PixelNode(
    val x: Int,
    val y: Int,
    val childDirectionX: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is PixelNode -> false
            x == other.x && y == other.y -> true
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + childDirectionX.hashCode()
        return result
    }
}