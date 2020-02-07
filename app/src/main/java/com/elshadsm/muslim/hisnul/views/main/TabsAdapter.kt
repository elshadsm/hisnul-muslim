package com.elshadsm.muslim.hisnul.views.main

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {

  override fun getItemCount() = 2

  override fun createFragment(position: Int) = when (position) {
    0 -> TitleTabFragment.newInstance()
    else -> BookmarkTabFragment.newInstance()
  }

}