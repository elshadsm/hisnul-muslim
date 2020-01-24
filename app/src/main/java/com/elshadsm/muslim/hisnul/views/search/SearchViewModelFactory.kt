package com.elshadsm.muslim.hisnul.views.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
      return SearchViewModel(context) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }

}
