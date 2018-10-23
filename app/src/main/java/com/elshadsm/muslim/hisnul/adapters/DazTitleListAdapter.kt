package com.elshadsm.muslim.hisnul.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.elshadsm.muslim.hisnul.R

class DazTitleListAdapter(private val context: Context) :
        RecyclerView.Adapter<DazTitleListAdapter.RecyclerViewHolder>() {

    private val titleList: Array<String> = context.resources.getStringArray(R.array.daz_title_list)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
                .inflate(R.layout.daz_title_list_item, parent, false)
        return RecyclerViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return titleList.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.number.text = (position + 1).toString().padStart(context.resources.getInteger(R.integer.daz_title_number_pad))
        holder.title.text = titleList[position]
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val number = itemView.findViewById<TextView>(R.id.daz_title_li_number)!!
        val title = itemView.findViewById<TextView>(R.id.daz_title_li_title)!!

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val clickedPosition = adapterPosition
            println(clickedPosition)
        }
    }
}