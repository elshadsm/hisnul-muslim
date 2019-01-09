package com.elshadsm.muslim.hisnul.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.AppBarLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elshadsm.muslim.hisnul.BR
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.databinding.ActivityDazViewBinding
import com.elshadsm.muslim.hisnul.models.DAZ_EXTRA_NAME
import com.elshadsm.muslim.hisnul.models.DazData

class DazViewActivity : AppCompatActivity() {

  private var menu: Menu? = null
  private lateinit var binding: ActivityDazViewBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_daz_view)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

  private fun applyConfiguration() {
    if (intent.hasExtra(DAZ_EXTRA_NAME)) {
      val data = intent.extras!!
      val dazData: DazData? = data.getParcelable(DAZ_EXTRA_NAME)
      binding.setVariable(BR.dazData, dazData)
      binding.executePendingBindings()
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

}
