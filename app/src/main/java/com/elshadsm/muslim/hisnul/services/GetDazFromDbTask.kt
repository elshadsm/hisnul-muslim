package com.elshadsm.muslim.hisnul.services

import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.activities.DazViewActivity
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Dhikr
import java.lang.ref.WeakReference

class GetDazFromDbTask(private val reference: WeakReference<DazViewActivity>, private val titleId: Int) :
    AsyncTask<Void, Void, List<Dhikr>>() {

  override fun doInBackground(vararg params: Void?): List<Dhikr> {
    reference.get()?.let {
      val appDataBase = AppDataBase.getInstance(it)
      return appDataBase.dhikrDao().getDhikr(titleId)
    }
    return listOf()
  }

  override fun onPostExecute(dazDataList: List<Dhikr>) {
    reference.get()?.updateData(dazDataList)
  }

}
