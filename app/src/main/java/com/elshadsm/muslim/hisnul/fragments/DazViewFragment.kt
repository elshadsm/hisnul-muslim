package com.elshadsm.muslim.hisnul.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.listeners.OnGestureListenerAdapter
import com.elshadsm.muslim.hisnul.models.DAZ_PARCEABLE_NAME
import kotlinx.android.synthetic.main.fragment_daz_view.*

class DazViewFragment(gestureListener: OnGestureListenerAdapter) : Fragment() {

  private var dhikr: Dhikr? = null
  private var gestureDetector = GestureDetector(context, gestureListener)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      dhikr = it.getParcelable(DAZ_PARCEABLE_NAME)
    }
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_daz_view, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    fragmentDazViewNestedScrollView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    dhikr?.let {
      arabic.text = it.arabic
      compiled.text = it.compiled
      translation.text = it.translation
      reference.text = it.reference
    }
  }

}
