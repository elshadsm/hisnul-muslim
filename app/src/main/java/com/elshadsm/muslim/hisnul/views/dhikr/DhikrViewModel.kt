package com.elshadsm.muslim.hisnul.views.dhikr

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.listeners.GeneralListenersManager
import kotlinx.coroutines.*

class DhikrViewModel(private val context: Context) : ViewModel() {

  private var viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  var dhikrList = MutableLiveData<List<Dhikr>>()
  var bookmarkUpdated = MutableLiveData<Boolean>()
  var currentPage: Int = 0
  private var titleId: Int = 0

  val currentDhikr: Dhikr?
    get() = dhikrList.value?.get(currentPage)

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  fun initializeData(titleId: Int) {
    this.titleId = titleId
    initializeDhikrList()
  }

  fun updateBookmark() {
    currentDhikr?.let {
      it.bookmarked = !it.bookmarked
      uiScope.launch {
        updateBookmarkInDatabase(it._id, it.bookmark)
        GeneralListenersManager.notifyBookmarkUpdated()
      }
      bookmarkUpdated.value = true
    }
  }

  fun shareDhikr() {
    val body = getShareBody()
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.share_option_title))
    intent.putExtra(Intent.EXTRA_TEXT, body)
    context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.option_share)))
  }

  fun isBookmarked() = currentDhikr?.bookmarked ?: false

  private fun getShareBody() = "${currentDhikr?.arabic}\n\n" +
      "${currentDhikr?.compiled}\n\n" +
      "${currentDhikr?.translation}\n\n" +
      "${currentDhikr?.reference}"

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

  private suspend fun updateBookmarkInDatabase(id: Int, bookmark: Int) = withContext(Dispatchers.IO) {
    val dataBase = AppDataBase.getInstance(context)
    dataBase.dhikrDao().updateBookmark(id, bookmark)
  }

}
