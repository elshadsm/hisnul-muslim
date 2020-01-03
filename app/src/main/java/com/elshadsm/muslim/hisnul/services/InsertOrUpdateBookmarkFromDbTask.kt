package com.elshadsm.muslim.hisnul.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.models.BookmarkOperation

@SuppressLint("StaticFieldLeak")
class InsertOrUpdateBookmarkFromDbTask(private val context: Context,
                                       private val bookmark: Bookmark,
                                       private val bookmarkOperation: BookmarkOperation) : AsyncTask<Void, Void, Unit>() {

  override fun doInBackground(vararg params: Void?) {
    val appDataBase = AppDataBase.getInstance(context)
    if (bookmarkOperation === BookmarkOperation.INSERT) {
      appDataBase.bookmarkDao().insertBookmark(bookmark)
    } else {
      appDataBase.bookmarkDao().deleteBookmark(bookmark)
    }
  }

}
