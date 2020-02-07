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
import androidx.recyclerview.widget.RecyclerView
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.views.dhikr.DhikrViewActivity
import com.elshadsm.muslim.hisnul.models.DHIKR_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DHIKR_PAGINATION_START_NUMBER
import com.elshadsm.muslim.hisnul.models.DHIKR_TITLE_EXTRA_NAME
import kotlinx.android.synthetic.main.bookmark_title_list_item.view.*
import kotlinx.android.synthetic.main.dhikr_title_list_item.view.numberView
import kotlinx.android.synthetic.main.dhikr_title_list_item.view.titleView

class BookmarkTabAdapter(private val context: Context, private val viewModel: BookmarkTabViewModel) :
    RecyclerView.Adapter<BookmarkTabAdapter.RecyclerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
        .inflate(R.layout.bookmark_title_list_item, parent, false)
    return RecyclerViewHolder(layoutInflater)
  }

  override fun getItemCount(): Int = viewModel.bookmarkList.value?.size ?: 0

  override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
    getBookmark(position)?.let { holder.bind(it) }
  }

  private fun getBookmark(position: Int) = viewModel.bookmarkList.value?.get(position)

  inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val numberView = itemView.numberView
    private val titleView = itemView.titleView
    private val optionIcon = itemView.optionIcon
    private val closeIcon = itemView.closeIcon
    private val deleteIcon = itemView.deleteIcon
    private var expanded = false

    init {
      itemView.setOnClickListener(this)
      optionIcon.setOnClickListener { expand() }
      closeIcon.setOnClickListener { expand() }
      deleteIcon.setOnClickListener { delete() }
    }

    override fun onClick(view: View) {
      getBookmark(adapterPosition)?.let {
        val intent = Intent(view.context, DhikrViewActivity::class.java)
        intent.putExtra(DHIKR_ID_EXTRA_NAME, it.titleId)
        intent.putExtra(DHIKR_TITLE_EXTRA_NAME, it.title)
        intent.putExtra(DHIKR_PAGINATION_START_NUMBER, it.dhikrNumber)
        view.context.startActivity(intent)
      }
    }

    fun bind(bookmark: Bookmark) {
      expanded = false
      itemView.content.translationX = 0f
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

    private fun expand() {
      val translationX = if (expanded) 0f else -(2 * itemView.width / 5f)
      expanded = !expanded
      itemView.content.animate().translationX(translationX)
    }

    private fun delete() {
      getBookmark(adapterPosition)?.let {
        viewModel.deleteBookmark(it.dhikrId)
      }
    }

  }

}
