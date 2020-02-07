package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TitleTabViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TitleTabViewModel::class.java)) {
      return TitleTabViewModel(context) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }

}
