package com.elshadsm.muslim.hisnul.views.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.elshadsm.muslim.hisnul.R
import kotlinx.android.synthetic.main.bookmark_tab_fragment.*

class BookmarkTabFragment : Fragment() {

  companion object {
    fun newInstance() = BookmarkTabFragment()
  }

  private lateinit var viewModel: BookmarkTabViewModel
  private lateinit var viewModelFactory: BookmarkTabViewModelFactory

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.bookmark_tab_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModelFactory = BookmarkTabViewModelFactory(requireContext())
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(BookmarkTabViewModel::class.java)
    recyclerView.adapter = BookmarkTabAdapter(requireContext(), viewModel)
    viewModel.bookmarkList.observe(requireActivity(), Observer {
      recyclerView.adapter?.notifyDataSetChanged()
      message.visibility = if (viewModel.bookmarkList.value?.isNotEmpty() != false) View.GONE else View.VISIBLE
    })
  }

}
