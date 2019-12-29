package com.elshadsm.muslim.hisnul.services

import android.os.AsyncTask
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Dhikr
import java.lang.ref.WeakReference

class GetDhikrFromDbTask(private val reference: WeakReference<DhikrViewActivity>, private val titleId: Int) :
    AsyncTask<Void, Void, List<Dhikr>>() {

  override fun doInBackground(vararg params: Void?): List<Dhikr> {
    reference.get()?.let {
      val appDataBase = AppDataBase.getInstance(it)
      return appDataBase.dhikrDao().getDhikr(titleId)
    }
    return listOf()
  }

  override fun onPostExecute(dhikrDataList: List<Dhikr>) {
    reference.get()?.updateData(dhikrDataList)
  }

}
