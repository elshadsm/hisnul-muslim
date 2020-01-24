package com.elshadsm.muslim.hisnul.views.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Title
import kotlinx.coroutines.*

class SearchViewModel(private val context: Context) : ViewModel() {

  private var viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  var titleList = MutableLiveData<List<Title>>()
  var state = MutableLiveData<SearchState>()

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun handleQuery(query: String?) {
    query?.let {
      if (it.length > 2) {
        queryTitleList(it)
        return
      }
    }
    updateState(query)
  }

  private fun updateState(query: String?) {
    query?.let {
      when {
        query.isEmpty() -> state.value = SearchState.EMPTY
        query.length < 3 -> state.value = SearchState.INCOMPLETE
        titleList.value?.isEmpty() != false -> state.value = SearchState.NOT_FOUND
        titleList.value?.isNotEmpty() != false -> state.value = SearchState.FOUND
      }
    } ?: run {
      state.value = SearchState.EMPTY
    }
  }

  private fun queryTitleList(query: String) {
    uiScope.launch {
      titleList.value = getTitleListFromDatabase(query)
      updateState(query)
    }
  }

  private suspend fun getTitleListFromDatabase(query: String) = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    val list = dataBase.titleDao().search(query)
    list
  }

}
