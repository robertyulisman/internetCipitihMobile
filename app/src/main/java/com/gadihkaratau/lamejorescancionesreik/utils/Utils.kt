package com.gadihkaratau.lamejorescancionesreik.utils

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import com.gadihkaratau.lamejorescancionesreik.R
import com.bumptech.glide.Glide

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun ImageView.loadImageFromUrl(url: String) {
    Glide.with(this)
            .load(url)
            .placeholder(R.drawable.photo_male_3)
            .error(R.drawable.photo_male_3)
            .into(this)
}

fun String.toTextHtml(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this, FROM_HTML_MODE_COMPACT)
    else
        Html.fromHtml(this)
}


