package com.elshadsm.muslim.hisnul.views.dhikr

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.models.BookmarkOperation
import com.elshadsm.muslim.hisnul.services.GetBookmarkFromDbTask
import com.elshadsm.muslim.hisnul.services.GetDhikrFromDbTask
import com.elshadsm.muslim.hisnul.services.InsertOrUpdateBookmarkFromDbTask

class DhikrViewModel : ViewModel() {

  var dhikrList = MutableLiveData<List<Dhikr>>()
  var bookmarkList = MutableLiveData<MutableList<Bookmark>>()
  var currentPage: Int = 0
  var titleId: Int = 0

  val currentDhikr: Dhikr?
    get() = dhikrList.value?.get(currentPage)
  private val currentDhikrId: Int
    get() = currentDhikr?._id ?: -1

  fun applyConfiguration(context: Context, titleId: Int) {
    this.titleId = titleId
    GetDhikrFromDbTask(context, this, titleId).execute()
    GetBookmarkFromDbTask(context, this, titleId).execute()
  }

  fun updateBookmark(context: Context) {
    val operation: BookmarkOperation
    var bookmark = getSelectedBookmark()
    if (bookmark == null) {
      operation = BookmarkOperation.INSERT
      bookmark = Bookmark(titleId = titleId, dhikrId = currentDhikrId)
      bookmarkList.value?.add(bookmark)
    } else {
      operation = BookmarkOperation.DELETE
      bookmarkList.value?.removeIf { it.dhikrId == currentDhikrId }
    }
    InsertOrUpdateBookmarkFromDbTask(context, bookmark, operation).execute()
    bookmarkList.notifyObserver()
  }

  fun shareDhikr(context: Context) {
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

}
