package com.elshadsm.muslim.hisnul.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.listeners.DhikrEvents.AudioUiListener
import com.elshadsm.muslim.hisnul.listeners.OnGestureListenerAdapter
import com.elshadsm.muslim.hisnul.models.AudioUiState
import com.elshadsm.muslim.hisnul.models.AudioUiState.*
import com.elshadsm.muslim.hisnul.models.DHIKR_PARCEABLE_NAME
import kotlinx.android.synthetic.main.fragment_dhikr_view.*
import kotlin.math.roundToInt

class DhikrViewFragment(gestureListener: OnGestureListenerAdapter) : Fragment(), AudioUiListener {

  private var dhikr: Dhikr? = null
  private var gestureDetector = GestureDetector(context, gestureListener)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      dhikr = it.getParcelable(DHIKR_PARCEABLE_NAME)
    }
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_dhikr_view, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    fragmentDhikrViewNestedScrollView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    dhikr?.let {
      arabic.text = it.arabic
      compiled.text = it.compiled
      translation.text = it.translation
      reference.text = it.reference
    }
  }

  override fun onStateChange(state: AudioUiState) {
    if(space == null) return
    val params = space.layoutParams
    params.height = getSpaceHeight(state)
    space.layoutParams = params
  }

  private fun getSpaceHeight(state: AudioUiState): Int {
    val resources = this.activity?.resources
    return when (state) {
      HIDDEN -> resources?.getDimension(R.dimen.bottom_padding_in_hidden_state)?.roundToInt()
      DOWNLOAD -> resources?.getDimension(R.dimen.bottom_padding_in_play_state)?.roundToInt()
      PLAY -> resources?.getDimension(R.dimen.bottom_padding_in_play_state)?.roundToInt()
      EXPANDED -> resources?.getDimension(R.dimen.bottom_padding_in_expanded_state)?.roundToInt()
      COLLAPSED -> resources?.getDimension(R.dimen.bottom_padding_in_collapsed_state)?.roundToInt()
    } ?: 0
  }

}
