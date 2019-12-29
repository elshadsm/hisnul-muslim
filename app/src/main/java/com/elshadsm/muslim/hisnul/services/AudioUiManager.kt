package com.elshadsm.muslim.hisnul.services

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.ktx.toPixel
import com.elshadsm.muslim.hisnul.models.AudioUiState
import kotlinx.android.synthetic.main.activity_dhikr_view.*

class AudioUiManager(private val activity: DhikrViewActivity) {

  var supported: Boolean = true
  var enabled: Boolean = true
  var state: AudioUiState = AudioUiState.PLAY

  fun open() {
    switchToExpandedState()
  }

  fun close() {
    switchToPlayState()
    activity.exoPlayer?.stop()
    restoreTransform()
  }

  fun reset() {
    if (supported && enabled) {
      switchToPlayState()
    } else {
      switchToHiddenState()
    }
    activity.exoPlayer?.stop()
    restoreTransform()
  }

  fun enable() {
    enabled = true
    switchToPlayState()
    val menuItem = activity.menu?.findItem(R.id.option_audio)
    menuItem?.icon = ContextCompat.getDrawable(activity, R.drawable.ic_volume_off_white_24dp)
  }

  fun disable() {
    enabled = false
    switchToHiddenState()
    val menuItem = activity.menu?.findItem(R.id.option_audio)
    menuItem?.icon = ContextCompat.getDrawable(activity, R.drawable.ic_volume_up_white_24dp)
    activity.exoPlayer?.stop()
    restoreTransform()
  }

  fun isOpen() = (state == AudioUiState.EXPANDED || state == AudioUiState.COLLAPSED)

  fun transform() {
    updateTransformInitialUi()
    listOf(activity.playerView, activity.playerCloseView, activity.playerTransformView).forEach { animateView(it) }
  }

  private fun updateTransformInitialUi() {
    if (state == AudioUiState.COLLAPSED) {
      activity.playerCollapsedView.visibility = View.GONE
      activity.playerView.visibility = View.VISIBLE
    }
  }

  private fun animateView(view: View) {
    var animatorSet: AnimatorSet? = null
    val float = getDistance().toPixel(activity)
    ObjectAnimator
        .ofFloat(view, "translationY", float)
        .apply {
          if (view == activity.playerView) {
            animatorSet = getTransformAnimatorSet(this)
          }
          animatorSet?.start() ?: start()
        }
  }

  private fun getDistance(): Float {
    val typedValue = TypedValue()
    if (state == AudioUiState.COLLAPSED) {
      activity.resources.getValue(R.dimen.zero_float, typedValue, true)
    } else {
      activity.resources.getValue(R.dimen.player_collapse_distance, typedValue, true)
    }
    return typedValue.float
  }

  private fun getTransformAnimatorSet(objectAnimator: ObjectAnimator) = AnimatorSet().apply {
    play(objectAnimator)
    addListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        updateTransformIcon()
        if (state == AudioUiState.COLLAPSED) {
          switchToExpandedState()
        } else {
          switchToCollapsedState()
        }
      }
    })
  }

  private fun updateTransformIcon() {
    ContextCompat.getDrawable(activity, if (state == AudioUiState.COLLAPSED)
      R.drawable.ic_collapse_player_white_24dp else R.drawable.ic_expand_player_white_24dp).let {
      activity.playerExpandCollapseIcon.setImageDrawable(it)
    }
  }

  private fun restoreTransform() {
    val typedValue = TypedValue()
    activity.resources.getValue(R.dimen.zero_float, typedValue, true)
    listOf(activity.playerView, activity.playerCloseView, activity.playerTransformView).forEach {
      it.translationY = typedValue.float
    }
    ContextCompat.getDrawable(activity, R.drawable.ic_collapse_player_white_24dp).let {
      activity.playerExpandCollapseIcon.setImageDrawable(it)
    }
  }

  private fun switchToHiddenState() {
    state = AudioUiState.HIDDEN
    activity.playerView.visibility = View.GONE
    activity.playerCollapsedView.visibility = View.GONE
    activity.playerTransformView.visibility = View.GONE
    activity.playerCloseView.visibility = View.GONE
    activity.playFab.hide()
    activity.playFab.visibility = View.GONE
  }

  private fun switchToPlayState() {
    state = AudioUiState.PLAY
    activity.playerView.visibility = View.GONE
    activity.playerCollapsedView.visibility = View.GONE
    activity.playerTransformView.visibility = View.GONE
    activity.playerCloseView.visibility = View.GONE
    activity.playFab.show()
    activity.playFab.visibility = View.VISIBLE
  }

  private fun switchToExpandedState() {
    state = AudioUiState.EXPANDED
    activity.playerView.visibility = View.VISIBLE
    activity.playerCollapsedView.visibility = View.GONE
    activity.playerTransformView.visibility = View.VISIBLE
    activity.playerCloseView.visibility = View.VISIBLE
    activity.playFab.hide()
    activity.playFab.visibility = View.GONE
  }

  fun switchToCollapsedState() {
    state = AudioUiState.COLLAPSED
    activity.playerView.visibility = View.GONE
    activity.playerCollapsedView.visibility = View.VISIBLE
    activity.playerTransformView.visibility = View.VISIBLE
    activity.playerCloseView.visibility = View.VISIBLE
    activity.playFab.hide()
    activity.playFab.visibility = View.GONE
  }

}
