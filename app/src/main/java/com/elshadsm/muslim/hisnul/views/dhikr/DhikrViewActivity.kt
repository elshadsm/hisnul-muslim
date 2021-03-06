package com.elshadsm.muslim.hisnul.views.dhikr

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.models.*
import com.elshadsm.muslim.hisnul.services.*
import kotlinx.android.synthetic.main.activity_dhikr_view.*

class DhikrViewActivity : AppCompatActivity() {

  lateinit var audioUiManager: AudioUiManager
  lateinit var audioManager: AudioManager
  lateinit var viewModel: DhikrViewModel
  lateinit var viewModelFactory: DhikrViewModelFactory
  val events = DhikrEvents()
  var menu: Menu? = null

  private lateinit var onAudioComplete: BroadcastReceiver
  private lateinit var pagerAdapter: DhikrViewAdapter
  private val permissionsManager = PermissionsManager(this)
  private var paginationStartNumber = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initializeUi()
    applyConfiguration()
    registerEventHandlers()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.dhikr_view_actions, menu)
    this.menu = menu
    updateBookmarkOptionIcon()
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.option_audio -> {
        handleAudioOptionSelect()
        return true
      }
      R.id.option_bookmark -> {
        viewModel.updateBookmark()
        return true
      }
      R.id.option_share -> {
        viewModel.shareDhikr()
        return true
      }
      R.id.option_settings -> {
        return true
      }
      android.R.id.home -> {
        finish()
        return true
      }
    }
    return super.onOptionsItemSelected(item!!)
  }

  override fun onStart() {
    super.onStart()
    audioManager.initializeExoPlayer()
  }

  override fun onResume() {
    super.onResume()
    audioManager.ensureExoPlayer()
  }

  override fun onStop() {
    super.onStop()
    if (audioUiManager.isOpen()) {
      audioUiManager.close()
    }
    audioManager.releaseExoPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(onAudioComplete)
    audioManager.releaseExoPlayer()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    permissionsManager.handleRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  fun handleAudioOptionSelect(tap: Boolean = false) {
    if (!audioUiManager.supported) return audioUiManager.disable()
    when {
      audioUiManager.isOpen() -> {
        if (!tap) audioUiManager.disable()
      }
      audioUiManager.enabled -> audioUiManager.disable()
      else -> audioUiManager.enable()
    }
  }

  private fun initializeUi() {
    setContentView(R.layout.activity_dhikr_view)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration() {
    viewModelFactory = DhikrViewModelFactory(this)
    viewModel = ViewModelProviders.of(this, viewModelFactory).get(DhikrViewModel::class.java)
    intent.extras?.getInt(DHIKR_ID_EXTRA_NAME)?.let { viewModel.initializeData(it) }
    pagerAdapter = DhikrViewAdapter(supportFragmentManager, this)
    intent.extras?.getString(DHIKR_TITLE_EXTRA_NAME)?.let {
      toolbarTitle.text = it
      ctlTitle.text = it
    }
    intent.extras?.getInt(DHIKR_PAGINATION_START_NUMBER, 1)?.let { paginationStartNumber = it - 1 }
    audioManager = AudioManager(this)
    audioUiManager = AudioUiManager(this)
    permissionsManager.start()
  }

  private fun registerEventHandlers() {
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener())
    viewPager.addOnPageChangeListener(PageChangeListener())
    onAudioComplete = DownloadCompleteReceiver()
    registerReceiver(onAudioComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    playFab.setOnClickListener { audioManager.play() }
    playerClose.setOnClickListener { audioUiManager.close() }
    playerTransform.setOnClickListener { audioUiManager.transform() }
    viewModel.dhikrList.observe(this, Observer {
      handleDhikrListUpdate()
      updateBookmarkOptionIcon()
    })
    viewModel.bookmarkUpdated.observe(this, Observer { updateBookmarkOptionIcon() })
  }

  private fun hideActions() {
    arrayOf(R.id.option_audio, R.id.option_bookmark, R.id.option_share, R.id.option_settings)
        .forEach { menu?.findItem(it)?.isVisible = false }
  }

  private fun showActions() {
    arrayOf(R.id.option_audio, R.id.option_bookmark, R.id.option_share, R.id.option_settings)
        .forEach { menu?.findItem(it)?.isVisible = true }
  }

  private fun updateTitleAndPagination(toolbarCollapsed: Boolean) {
    ctlTitle.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    ctlPagination.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    toolbarTitle.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
    toolbarPagination.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
  }

  private fun handleDhikrListUpdate() {
    pagerAdapter.createFragments(viewModel)
    viewPager.adapter = pagerAdapter
    updatePagination(paginationStartNumber, pagerAdapter.count)
  }

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val paginationText = String.format(resources.getString(R.string.dhikr_view_pagination), (currentPage + 1), totalPage)
    ctlPagination.text = paginationText
    toolbarPagination.text = paginationText
    viewModel.currentPage = currentPage
    audioUiManager.reset()
    updateBookmarkOptionIcon()
    viewPager.currentItem = currentPage
  }

  private fun updateBookmarkOptionIcon() {
    val menuItem = menu?.findItem(R.id.option_bookmark)
    if (viewModel.isBookmarked()) {
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white_24dp)
    } else {
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_white_24dp)
    }
  }

  inner class OnOffsetChangedListener : AppBarLayout.OnOffsetChangedListener {
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
  }

  inner class PageChangeListener : ViewPager.SimpleOnPageChangeListener() {
    override fun onPageSelected(position: Int) = updatePagination(position, pagerAdapter.count)
  }

  inner class DownloadCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      audioManager.onDownloadComplete(intent)
    }
  }

}
