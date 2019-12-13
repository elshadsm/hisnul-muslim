package com.elshadsm.muslim.hisnul.listeners

import android.view.GestureDetector
import android.view.MotionEvent

open class OnGestureListenerAdapter : GestureDetector.OnGestureListener {

  override fun onDown(event: MotionEvent): Boolean = false

  override fun onShowPress(event: MotionEvent) = Unit // nothing to do here

  override fun onSingleTapUp(event: MotionEvent): Boolean = false

  override fun onScroll(
    eventOne: MotionEvent, eventTwo: MotionEvent,
    distanceX: Float, distanceY: Float
  ): Boolean = false

  override fun onLongPress(event: MotionEvent) = Unit // nothing to do here

  override fun onFling(
    eventOne: MotionEvent, eventTwo: MotionEvent,
    velocityX: Float, velocityY: Float
  ): Boolean = false

}
