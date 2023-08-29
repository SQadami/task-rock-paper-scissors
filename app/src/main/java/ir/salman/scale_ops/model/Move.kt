package ir.salman.scale_ops.model

import ir.salman.scale_ops.utils.dpToPx

enum class Move {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

fun randomMove() =
    when (randomDirection()) {
        0 -> Move.UP
        1 -> Move.DOWN
        2 -> Move.LEFT
        3 -> Move.RIGHT
        else -> throw RuntimeException("invalid direction on random move")
    }

private fun randomDirection() = (0 until Move.values().size).random()

val DIRECTION_STEP = dpToPx(4)