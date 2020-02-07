package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookmarkTabViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(BookmarkTabViewModel::class.java)) {
      return BookmarkTabViewModel(context) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }

}
