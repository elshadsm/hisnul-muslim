package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.listeners.GeneralListenersManager
import com.elshadsm.muslim.hisnul.listeners.GeneralListenersManager.Companion.BookmarkListener
import kotlinx.coroutines.*

class BookmarkTabViewModel(private val context: Context) : ViewModel(), BookmarkListener {

  private var viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  var bookmarkList = MutableLiveData<MutableList<Bookmark>>()

  init {
    GeneralListenersManager.addBookmarkListener(this)
    updateTitleList()
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  override fun onBookmarkUpdate() {
    updateTitleList()
  }

  fun deleteBookmark(id: Int) {
    uiScope.launch {
      updateBookmarkInDatabase(id, 0)
      bookmarkList.value?.removeIf { it.dhikrId == id }
      bookmarkList.value = bookmarkList.value
    }
  }

  private fun updateTitleList() {
    uiScope.launch {
      bookmarkList.value = getBookmarkListFromDatabase()
    }
  }

  private suspend fun getBookmarkListFromDatabase() = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    val list = dataBase.bookmarkDao().getAll()
    list
  }

  private suspend fun updateBookmarkInDatabase(id: Int, bookmark: Int) = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    dataBase.dhikrDao().updateBookmark(id, bookmark)
  }

}
