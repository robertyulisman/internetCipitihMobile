package com.gadihkaratau.lamejorescancionesreik.barqiads.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun View.visible() {
    visibility = View.VISIBLE
}
