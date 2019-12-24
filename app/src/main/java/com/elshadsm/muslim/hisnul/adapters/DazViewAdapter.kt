package com.elshadsm.muslim.hisnul.adapters

import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.elshadsm.muslim.hisnul.activities.DazViewActivity
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.fragments.DazViewFragment
import com.elshadsm.muslim.hisnul.listeners.OnGestureListenerAdapter
import com.elshadsm.muslim.hisnul.models.DAZ_PARCEABLE_NAME

class DazViewAdapter(fragmentManager: FragmentManager, dazViewActivity: DazViewActivity) : FragmentStatePagerAdapter(fragmentManager) {

  private var dazDataList = listOf<Dhikr>()

  private val gestureListener = object : OnGestureListenerAdapter() {

    override fun onSingleTapUp(event: MotionEvent): Boolean {
      dazViewActivity.hideOrDisplayPlayOption(true)
      return true
    }

  }

  override fun getItem(position: Int): Fragment {
    val arguments = Bundle()
    arguments.putParcelable(DAZ_PARCEABLE_NAME, dazDataList[position])
    val fragment = DazViewFragment(gestureListener)
    fragment.arguments = arguments
    return fragment
  }

  override fun getCount(): Int = dazDataList.size

  fun setData(data: List<Dhikr>) {
    dazDataList = data
    notifyDataSetChanged()
  }

  fun getDataAt(index: Int) = dazDataList[index]

}
