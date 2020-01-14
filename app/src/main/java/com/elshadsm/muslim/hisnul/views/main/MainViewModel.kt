package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Title
import kotlinx.coroutines.*

class MainViewModel(private val context: Context) : ViewModel() {

  private var viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  var titleList = MutableLiveData<List<Title>>()

  init {
    initializeTitleList()
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  private fun initializeTitleList() {
    uiScope.launch {
      titleList.value = getTitleListFromDatabase()
    }
  }

  private suspend fun getTitleListFromDatabase() = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    val list = dataBase.titleDao().getAll()
    list
  }

}
