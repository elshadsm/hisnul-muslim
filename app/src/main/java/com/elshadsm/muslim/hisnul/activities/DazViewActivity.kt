package com.elshadsm.muslim.hisnul.activities

import android.os.AsyncTask
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elshadsm.muslim.hisnul.BR
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.adapters.DazViewAdapter
import com.elshadsm.muslim.hisnul.database.AppDataBase
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.databinding.ActivityDazViewBinding
import com.elshadsm.muslim.hisnul.models.DAZ_ID_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazViewActivity : AppCompatActivity() {

  private var menu: Menu? = null
  private lateinit var binding: ActivityDazViewBinding
  private lateinit var pagerAdapter: DazViewAdapter

  private lateinit var appDataBase: AppDataBase

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    init()
    applyConfiguration()
    registerEventHandlers()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.daz_view_actions, menu)
    this.menu = menu
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.action_hide_or_display_play -> return true
      R.id.action_bookmark -> return true
      R.id.action_share -> return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun init() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_daz_view)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration() {
    if (intent.hasExtra(DAZ_ID_EXTRA_NAME)) {
      val data = intent.extras
      appDataBase = AppDataBase.getInstance(this)
      val dazId = data?.getInt(DAZ_ID_EXTRA_NAME) ?: 1
      pagerAdapter = DazViewAdapter(supportFragmentManager)
      GetDazFromDbTask(this, dazId).execute()
    }
  }

  private fun registerEventHandlers() {
    binding.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
      var isShow = false
      var scrollRange = -1

      override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (scrollRange == -1) {
          scrollRange = appBarLayout.totalScrollRange
        }
        if (scrollRange + verticalOffset == 0) {
          hideActions()
          updateTitleAndPagination(true)
          isShow = true
        } else if (isShow) {
          showActions()
          updateTitleAndPagination(false)
          isShow = false
        }
      }
    })
    binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
      override fun onPageSelected(position: Int) = updatePagination(position + 1, pagerAdapter.count)
    })
  }

  private fun hideActions() {
    arrayOf(R.id.action_hide_or_display_play, R.id.action_bookmark, R.id.action_share).forEach {
      menu?.findItem(it)?.isVisible = false
    }
  }

  private fun showActions() {
    arrayOf(R.id.action_hide_or_display_play, R.id.action_bookmark, R.id.action_share).forEach {
      menu?.findItem(it)?.isVisible = true
    }
  }

  private fun updateTitleAndPagination(toolbarCollapsed: Boolean) {
    binding.ctlTitle.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    binding.ctlPagination.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    binding.toolbarTitle.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
    binding.toolbarPagination.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
  }

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val pagination = String.format(resources.getString(R.string.daz_view_pagination), currentPage, totalPage)
    binding.toolbarPagination.text = pagination
    binding.ctlPagination.text = pagination
  }

  companion object {

    class GetDazFromDbTask(private val reference: DazViewActivity, private val titleId: Int) : AsyncTask<Void, Void, List<Dhikr>>() {

      override fun doInBackground(vararg params: Void?): List<Dhikr> = reference.appDataBase.dhikrDao().getDhikr(1)

      override fun onPostExecute(titleList: List<Dhikr>?) {
        val dazDataList = reference.appDataBase.dhikrDao().getDhikr(titleId)
        reference.pagerAdapter.setData(dazDataList)
        reference.binding.viewPager.adapter = reference.pagerAdapter
        reference.binding.setVariable(BR.dhikr, dazDataList[0])
        reference.binding.executePendingBindings()
        reference.updatePagination(1, reference.pagerAdapter.count)
      }

    }
  }

}
