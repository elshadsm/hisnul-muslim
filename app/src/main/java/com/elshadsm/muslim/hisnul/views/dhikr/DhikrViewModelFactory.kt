package com.elshadsm.muslim.hisnul.views.dhikr

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DhikrViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(DhikrViewModel::class.java)) {
      return DhikrViewModel(context) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }

}
