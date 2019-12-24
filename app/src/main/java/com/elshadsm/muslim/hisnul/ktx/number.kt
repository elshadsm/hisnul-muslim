package com.elshadsm.muslim.hisnul.ktx

import android.content.Context
import android.view.View

fun Number.toPixel(view: View) = this.toPixel(view.context)
fun Number.toPixel(context: Context) = this.toFloat() * context.resources.displayMetrics.density

fun Number.toDip(view: View) = this.toDip(view.context)
fun Number.toDip(context: Context) = this.toFloat() / context.resources.displayMetrics.density
