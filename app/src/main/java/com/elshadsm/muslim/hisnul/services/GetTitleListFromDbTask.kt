package com.elshadsm.muslim.hisnul.services

import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.views.main.DhikrTitleListAdapter
import com.elshadsm.muslim.hisnul.database.Title

class GetTitleListFromDbTask(private val adapter: DhikrTitleListAdapter) : AsyncTask<Void, Void, List<Title>>() {

  override fun doInBackground(vararg params: Void?): List<Title> = adapter.appDataBase.titleDao().getAll()

  override fun onPostExecute(titleList: List<Title>?) = adapter.setData(titleList)

}