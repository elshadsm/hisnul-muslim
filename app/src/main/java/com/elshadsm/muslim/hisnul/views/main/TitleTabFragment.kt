package com.elshadsm.muslim.hisnul.views.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.elshadsm.muslim.hisnul.R
import kotlinx.android.synthetic.main.activity_search.*

class TitleTabFragment : Fragment() {

  companion object {
    fun newInstance() = TitleTabFragment()
  }

  private lateinit var viewModel: TitleTabViewModel
  private lateinit var viewModelFactory: TitleTabViewModelFactory

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
      inflater.inflate(R.layout.title_tab_fragment, container, false)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModelFactory = TitleTabViewModelFactory(requireContext())
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(TitleTabViewModel::class.java)
    recyclerView.adapter = TitleTabAdapter(requireContext(), viewModel)
    viewModel.titleList.observe(requireActivity(), Observer { recyclerView.adapter?.notifyDataSetChanged() })
  }

}
