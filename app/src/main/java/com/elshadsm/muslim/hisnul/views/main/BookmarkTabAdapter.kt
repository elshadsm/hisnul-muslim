package com.elshadsm.muslim.hisnul.views.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.views.dhikr.DhikrViewActivity
import com.elshadsm.muslim.hisnul.models.DHIKR_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DHIKR_PAGINATION_START_NUMBER
import com.elshadsm.muslim.hisnul.models.DHIKR_TITLE_EXTRA_NAME
import kotlinx.android.synthetic.main.dhikr_title_list_item.view.*

class BookmarkTabAdapter(private val context: Context, private val viewModel: BookmarkTabViewModel) :
    RecyclerView.Adapter<BookmarkTabAdapter.RecyclerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        .inflate(R.layout.bookmark_title_list_item, parent, false)
    return RecyclerViewHolder(layoutInflater)
  }

  override fun getItemCount(): Int = viewModel.bookmarkList.value?.size ?: 0

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    getTitle(position)?.let { holder.bind(it) }
  }

  private fun getTitle(position: Int) = viewModel.bookmarkList.value?.get(position)

  inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val numberView: TextView = itemView.numberView
    private val titleView: TextView = itemView.titleView

    init {
      itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      getTitle(adapterPosition)?.let {
        val intent = Intent(view.context, DhikrViewActivity::class.java)
        intent.putExtra(DHIKR_ID_EXTRA_NAME, it.titleId)
        intent.putExtra(DHIKR_TITLE_EXTRA_NAME, it.title)
        intent.putExtra(DHIKR_PAGINATION_START_NUMBER, it.dhikrNumber)
        view.context.startActivity(intent)
      }
    }

    fun bind(bookmark: Bookmark) {
      val number = "(${bookmark.dhikrNumber})"
      val spannable = SpannableString("${bookmark.title} $number")
      val start = bookmark.title.length + 1
      spannable.setSpan(
          ForegroundColorSpan(Color.RED),
          start, start + number.length,
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
      numberView.text = bookmark.titleNumber.toString()
      titleView.text = spannable
    }

  }

}
