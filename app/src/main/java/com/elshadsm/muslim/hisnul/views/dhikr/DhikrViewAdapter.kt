package com.elshadsm.muslim.hisnul.views.dhikr

import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.listeners.OnGestureListenerAdapter
import com.elshadsm.muslim.hisnul.models.DHIKR_PARCEABLE_NAME

class DhikrViewAdapter(fragmentManager: FragmentManager, private val activity: DhikrViewActivity)
  : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  private var fragmentList = mutableListOf<Fragment>()

  private val gestureListener = object : OnGestureListenerAdapter() {

    override fun onSingleTapUp(event: MotionEvent): Boolean {
      activity.handleAudioOptionSelect(true)
      return true
    }

  }

  override fun getItem(position: Int) = fragmentList[position]

  override fun getCount(): Int = fragmentList.size

  fun createFragments(viewModel: DhikrViewModel) {
    viewModel.dhikrList.value?.forEach { createFragment(it) }
    notifyDataSetChanged()
  }

  private fun createFragment(dhikr: Dhikr) {
    val arguments = Bundle()
    arguments.putParcelable(DHIKR_PARCEABLE_NAME, dhikr)
    val fragment = DhikrViewFragment(gestureListener)
    fragment.arguments = arguments
    activity.events.addAudioUiListener(fragment)
    fragmentList.add(fragment)
  }

}
