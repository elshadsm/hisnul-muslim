package com.elshadsm.muslim.hisnul.adapters

import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.fragments.DhikrViewFragment
import com.elshadsm.muslim.hisnul.listeners.OnGestureListenerAdapter
import com.elshadsm.muslim.hisnul.models.DHIKR_PARCEABLE_NAME

class DhikrViewAdapter(fragmentManager: FragmentManager, dhikrViewActivity: DhikrViewActivity) : FragmentStatePagerAdapter(fragmentManager) {

  private var dhikrDataList = listOf<Dhikr>()

  private val gestureListener = object : OnGestureListenerAdapter() {

    override fun onSingleTapUp(event: MotionEvent): Boolean {
      dhikrViewActivity.handleAudioOptionSelect(true)
      return true
    }

  }

  override fun getItem(position: Int): Fragment {
    val arguments = Bundle()
    arguments.putParcelable(DHIKR_PARCEABLE_NAME, dhikrDataList[position])
    val fragment = DhikrViewFragment(gestureListener)
    fragment.arguments = arguments
    return fragment
  }

  override fun getCount(): Int = dhikrDataList.size

  fun setData(data: List<Dhikr>) {
    dhikrDataList = data
    notifyDataSetChanged()
  }

  fun getDataAt(index: Int) = dhikrDataList[index]

}
