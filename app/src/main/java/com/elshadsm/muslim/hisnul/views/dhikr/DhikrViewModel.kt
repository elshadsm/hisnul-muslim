package com.elshadsm.muslim.hisnul.views.dhikr

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.database.Dhikr
import kotlinx.coroutines.*

class DhikrViewModel(private val context: Context) : ViewModel() {

  private var viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  var dhikrList = MutableLiveData<List<Dhikr>>()
  var bookmarkList = MutableLiveData<MutableList<Bookmark>>()
  var currentPage: Int = 0
  var titleId: Int = 0

  val currentDhikr: Dhikr?
    get() = dhikrList.value?.get(currentPage)
  private val currentDhikrId: Int
    get() = currentDhikr?._id ?: -1

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun initializeData(titleId: Int) {
    this.titleId = titleId
    initializeDhikrList()
    initializeBookmarkList()
  }

  fun updateBookmark() {
    var bookmark = getSelectedBookmark()
    if (bookmark == null) {
      bookmark = Bookmark(titleId = titleId, dhikrId = currentDhikrId)
      bookmarkList.value?.add(bookmark)
      uiScope.launch { insertBookmark(bookmark) }
    } else {
      bookmarkList.value?.removeIf { it.dhikrId == currentDhikrId }
      uiScope.launch { deleteBookmark(bookmark) }
    }
    bookmarkList.notifyObserver()
  }

  fun shareDhikr() {
    val body = getShareBody()
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.share_option_title))
    intent.putExtra(Intent.EXTRA_TEXT, body)
    context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.option_share)))
  }

  fun isSelectedBookmark() = bookmarkList.value?.any { it.dhikrId == currentDhikrId } ?: false

  private fun getSelectedBookmark() = bookmarkList.value?.firstOrNull { it.dhikrId == currentDhikrId }

  private fun getShareBody() = "${currentDhikr?.arabic}\n\n" +
      "${currentDhikr?.compiled}\n\n" +
      "${currentDhikr?.translation}\n\n" +
      "${currentDhikr?.reference}"

  private fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
  }

  private fun initializeDhikrList() {
    uiScope.launch {
      dhikrList.value = getDhikrListFromDatabase()
    }
  }

  private suspend fun getDhikrListFromDatabase() = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    val list = dataBase.dhikrDao().getDhikr(titleId)
    list
  }

  private fun initializeBookmarkList() {
    uiScope.launch {
      bookmarkList.value = getBookmarkListFromDatabase()
    }
  }

  private suspend fun getBookmarkListFromDatabase() = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    val list = dataBase.bookmarkDao().getBookmark(titleId)
    list
  }

  private suspend fun insertBookmark(bookmark: Bookmark) = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    dataBase.bookmarkDao().insertBookmark(bookmark)
  }

  private suspend fun deleteBookmark(bookmark: Bookmark) = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    dataBase.bookmarkDao().deleteBookmark(bookmark)
  }

}
