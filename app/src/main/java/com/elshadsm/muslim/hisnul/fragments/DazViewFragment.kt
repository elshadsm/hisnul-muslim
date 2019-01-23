package com.elshadsm.muslim.hisnul.fragments

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elshadsm.muslim.hisnul.BR
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.databinding.FragmentDazViewBinding
import com.elshadsm.muslim.hisnul.models.DAZ_PARCEABLE_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazViewFragment : Fragment() {

  private var dazData: DazData? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      if (it.containsKey(DAZ_PARCEABLE_NAME)) {
        dazData = it.getParcelable(DAZ_PARCEABLE_NAME)
      }
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val binding = DataBindingUtil.inflate<FragmentDazViewBinding>(inflater, R.layout.fragment_daz_view, container, false)
    dazData?.let {
      binding.setVariable(BR.dazData, dazData)
      binding.executePendingBindings()
    }
    return binding.root
  }

}
