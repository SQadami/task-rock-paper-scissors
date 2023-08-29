package ir.salman.scale_ops.model

import ir.salman.scale_ops.widget.Widget

data class Toy(
    val id: Int,
    val type: Widget.Type,
    val x: Int,
    val y: Int,
    var prevMove: Move? = null,
    var nextMove: Move? = null,
)
