package com.elshadsm.muslim.hisnul.services

import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.activities.DazViewActivity
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.models.BookmarkOperation
import java.lang.ref.WeakReference

class InsertOrUpdateBookmarkFromDbTask(private val reference: WeakReference<DazViewActivity>,
                                       private val bookmark: Bookmark,
                                       private val bookmarkOperation: BookmarkOperation) :
    AsyncTask<Void, Void, Unit>() {

  override fun doInBackground(vararg params: Void?) {
    reference.get()?.let {
      val appDataBase = AppDataBase.getInstance(it)
      if (bookmarkOperation === BookmarkOperation.INSERT) {
        appDataBase.bookmarkDao().insertBookmark(bookmark)
      } else {
        appDataBase.bookmarkDao().deleteBookmark(bookmark)
      }
    }
  }

}
