package com.elshadsm.muslim.hisnul.services

import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import java.lang.ref.WeakReference

class GetBookmarkFromDbTask(private val reference: WeakReference<DhikrViewActivity>, private val titleId: Int) :
    AsyncTask<Void, Void, MutableList<Bookmark>>() {

  override fun doInBackground(vararg params: Void?): MutableList<Bookmark> {
    reference.get()?.let {
      val appDataBase = AppDataBase.getInstance(it)
      return appDataBase.bookmarkDao().getBookmark(titleId)
    }
    return mutableListOf()
  }

  override fun onPostExecute(bookmarkList: MutableList<Bookmark>) {
    reference.get()?.updateBookmarkList(bookmarkList)
  }

}
