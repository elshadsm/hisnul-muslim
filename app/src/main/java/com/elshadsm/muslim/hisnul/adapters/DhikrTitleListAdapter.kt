package com.elshadsm.muslim.hisnul.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Title
import com.elshadsm.muslim.hisnul.models.DHIKR_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DHIKR_TITLE_EXTRA_NAME
import com.elshadsm.muslim.hisnul.services.GetTitleListFromDbTask

class DhikrTitleListAdapter(private val context: Context) :
    RecyclerView.Adapter<DhikrTitleListAdapter.RecyclerViewHolder>() {

  private var titleList = listOf<Title>()

  val appDataBase: AppDataBase = AppDataBase.getInstance(context)

  init {
    GetTitleListFromDbTask(this).execute()
  }

  fun setData(data: List<Title>?) {
    data?.let {
      titleList = it
      notifyDataSetChanged()
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        .inflate(R.layout.dhikr_title_list_item, parent, false)
    return RecyclerViewHolder(layoutInflater)
  }

  override fun getItemCount(): Int = titleList.size

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    val title = titleList[position]
    holder.number.text = title.index.toString().padStart(context.resources.getInteger(R.integer.dhikr_title_number_pad))
    holder.title.text = title.text
  }

  inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val number = itemView.findViewById<TextView>(R.id.dhikr_title_li_number)!!
    val title = itemView.findViewById<TextView>(R.id.dhikr_title_li_title)!!

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      val intent = Intent(view.context, DhikrViewActivity::class.java)
      intent.putExtra(DHIKR_ID_EXTRA_NAME, titleList[adapterPosition]._id)
      intent.putExtra(DHIKR_TITLE_EXTRA_NAME, titleList[adapterPosition].text)
      view.context.startActivity(intent)
    }

  }

}
