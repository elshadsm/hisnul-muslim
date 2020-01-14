package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Title
import com.elshadsm.muslim.hisnul.views.dhikr.DhikrViewActivity
import com.elshadsm.muslim.hisnul.models.DHIKR_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DHIKR_TITLE_EXTRA_NAME
import kotlinx.android.synthetic.main.dhikr_title_list_item.view.*

class DhikrTitleListAdapter(private val context: Context, private val mainViewModel: MainViewModel) :
    RecyclerView.Adapter<DhikrTitleListAdapter.RecyclerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        .inflate(R.layout.dhikr_title_list_item, parent, false)
    return RecyclerViewHolder(layoutInflater)
  }

  override fun getItemCount(): Int = mainViewModel.titleList.value?.size ?: 0

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    getTitle(position)?.let { holder.bind(it) }
  }

  private fun getTitle(position: Int) = mainViewModel.titleList.value?.get(position)

  inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val numberView: TextView = itemView.numberView
    private val titleView: TextView = itemView.titleView

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      getTitle(adapterPosition)?.let {
        val intent = Intent(view.context, DhikrViewActivity::class.java)
        intent.putExtra(DHIKR_ID_EXTRA_NAME, it._id)
        intent.putExtra(DHIKR_TITLE_EXTRA_NAME, it.text)
        view.context.startActivity(intent)
      }
    }

    fun bind(title: Title) {
      val pad = context.resources.getInteger(R.integer.dhikr_title_number_pad)
      numberView.text = title.index.toString().padStart(pad)
      titleView.text = title.text
    }

  }

}
