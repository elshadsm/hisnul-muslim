package com.elshadsm.muslim.hisnul.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.views.dhikr.DhikrViewModel

@SuppressLint("StaticFieldLeak")
class GetDhikrFromDbTask(private val context: Context,
                         private val viewModel: DhikrViewModel,
                         private val titleId: Int) : AsyncTask<Void, Void, List<Dhikr>>() {

  override fun doInBackground(vararg params: Void?): List<Dhikr> {
    val appDataBase = AppDataBase.getInstance(context)
    return appDataBase.dhikrDao().getDhikr(titleId)
  }

  override fun onPostExecute(dhikrList: List<Dhikr>) {
    viewModel.dhikrList.value = dhikrList
  }

}
