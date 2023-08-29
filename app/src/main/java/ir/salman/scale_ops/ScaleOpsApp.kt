package ir.salman.scale_ops

import android.annotation.SuppressLint
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScaleOpsApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: ScaleOpsApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}