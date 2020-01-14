package com.elshadsm.muslim.hisnul.views.dhikr

import com.elshadsm.muslim.hisnul.models.AudioUiState

class DhikrEvents {

  interface AudioUiListener {
    fun onStateChange(state: AudioUiState)
  }

  private val audioUiListenerList = mutableListOf<AudioUiListener>()

  fun addAudioUiListener(listener: AudioUiListener) = audioUiListenerList.add(listener)

  fun notifyAudioUiStateChanged(state: AudioUiState, index: Int) {
    if (audioUiListenerList.size > index) {
      audioUiListenerList[index].onStateChange(state)
    }
  }

}
