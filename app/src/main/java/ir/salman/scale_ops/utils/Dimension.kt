package ir.salman.scale_ops.utils

import android.annotation.SuppressLint
import android.content.res.Resources
import ir.salman.scale_ops.ScaleOpsApp

fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels - statusBarHeight()

fun screenWidth() = Resources.getSystem().displayMetrics.widthPixels

@SuppressLint("InternalInsetResource", "DiscouragedApi")
fun statusBarHeight(): Int {
    var result = 0
    val resourceId: Int =
        ScaleOpsApp.INSTANCE.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = ScaleOpsApp.INSTANCE.resources.getDimensionPixelSize(resourceId)
    }
    return result
}