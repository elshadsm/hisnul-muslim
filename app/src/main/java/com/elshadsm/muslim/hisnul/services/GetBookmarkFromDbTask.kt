package com.elshadsm.muslim.hisnul.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.viewmodel.dhikrview.DhikrViewModel

@SuppressLint("StaticFieldLeak")
class GetBookmarkFromDbTask(private val context: Context,
                            private val viewModel: DhikrViewModel,
                            private val titleId: Int) : AsyncTask<Void, Void, MutableList<Bookmark>>() {

  override fun doInBackground(vararg params: Void?): MutableList<Bookmark> {
    val appDataBase = AppDataBase.getInstance(context)
    return appDataBase.bookmarkDao().getBookmark(titleId)
  }

  override fun onPostExecute(bookmarkList: MutableList<Bookmark>) {
    viewModel.bookmarkList.value = bookmarkList
  }

}
