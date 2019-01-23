package com.elshadsm.muslim.hisnul.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.elshadsm.muslim.hisnul.fragments.DazViewFragment
import com.elshadsm.muslim.hisnul.models.DAZ_PARCEABLE_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazViewAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

  private var dazDataList = listOf<DazData>()

  override fun getItem(position: Int): Fragment {
    val arguments = Bundle()
    arguments.putParcelable(DAZ_PARCEABLE_NAME, dazDataList[position])
    val fragment = DazViewFragment()
    fragment.arguments = arguments
    return fragment
  }

  override fun getCount(): Int = dazDataList.size

  fun setData(data: List<DazData>) {
    dazDataList = data
    notifyDataSetChanged()
  }

}
