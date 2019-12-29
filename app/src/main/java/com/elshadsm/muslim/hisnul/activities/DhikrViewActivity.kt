package com.elshadsm.muslim.hisnul.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Outline
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.content.ContextCompat
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.adapters.DhikrViewAdapter
import com.elshadsm.muslim.hisnul.database.Bookmark
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.models.*
import com.elshadsm.muslim.hisnul.services.*
import kotlinx.android.synthetic.main.activity_dhikr_view.*
import java.lang.ref.WeakReference

class DhikrViewActivity : AppCompatActivity() {

  lateinit var audioUiManager: AudioUiManager
  lateinit var audioManager: AudioManager
  lateinit var currentDhikr: Dhikr
  var menu: Menu? = null

  private val permissionsManager = PermissionsManager(this)
  private val paginationStartNumber = 0
  private var bookmarkList: MutableList<Bookmark> = mutableListOf()
  private var titleId: Int = 0
  private lateinit var onAudioComplete: BroadcastReceiver
  private lateinit var pagerAdapter: DhikrViewAdapter

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
    audioUiManager.reset()
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.option_audio -> {
        handleAudioOptionSelect()
        return true
      }
      R.id.option_bookmark -> {
        handleBookmarkOptionSelect()
        return true
      }
      R.id.option_share -> {
        handleShareOptionSelect()
        return true
      }
      R.id.option_settings -> {
        return true
      }
    }
    return super.onOptionsItemSelected(item)
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

  fun updateData(dhikrDataList: List<Dhikr>) {
    pagerAdapter.setData(dhikrDataList)
    viewPager.adapter = pagerAdapter
    updatePagination(paginationStartNumber, pagerAdapter.count)
  }

  fun updateBookmarkList(bookmarkList: MutableList<Bookmark>) {
    this.bookmarkList = bookmarkList
    updateBookmarkOptionIcon()
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
    intent.extras?.getInt(DHIKR_ID_EXTRA_NAME)?.let {
      titleId = it
      GetDhikrFromDbTask(WeakReference(this), it).execute()
      GetBookmarkFromDbTask(WeakReference(this), it).execute()
    }
    pagerAdapter = DhikrViewAdapter(supportFragmentManager, this)
    intent.extras?.getString(DHIKR_TITLE_EXTRA_NAME)?.let {
      toolbarTitle.text = it
      ctlTitle.text = it
    }
    permissionsManager.start()
    applyPlayerOptionsConfiguration()
    audioManager = AudioManager(this)
    audioUiManager = AudioUiManager(this)
  }

  private fun registerEventHandlers() {
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener())
    viewPager.addOnPageChangeListener(PageChangeListener())
    onAudioComplete = DownloadCompleteReceiver()
    registerReceiver(onAudioComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    playFab.setOnClickListener(PlayListener())
    playerCloseView.setOnClickListener(PlayerCloseListener())
    playerTransformView.setOnClickListener(PlayerTransformListener())
  }

  private fun handleBookmarkOptionSelect() {
    var bookmark = bookmarkList.firstOrNull { it.dhikrId == currentDhikr._id }
    val operation: BookmarkOperation
    if (bookmark == null) {
      operation = BookmarkOperation.INSERT
      bookmark = Bookmark(titleId = titleId, dhikrId = currentDhikr._id)
      bookmarkList.add(bookmark)
    } else {
      operation = BookmarkOperation.DELETE
      bookmarkList.removeIf { it.dhikrId == currentDhikr._id }
    }
    InsertOrUpdateBookmarkFromDbTask(WeakReference(this), bookmark, operation).execute()
    updateBookmarkOptionIcon()
  }

  private fun handleShareOptionSelect() {
    val body = getShareBody()
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_option_title))
    intent.putExtra(Intent.EXTRA_TEXT, body)
    startActivity(Intent.createChooser(intent, resources.getString(R.string.option_share)))
  }

  private fun getShareBody(): String {
    return "${currentDhikr.arabic}\n\n${currentDhikr.compiled}\n\n${currentDhikr.translation}\n\n${currentDhikr.reference}"
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

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val paginationText = String.format(resources.getString(R.string.dhikr_view_pagination), (currentPage + 1), totalPage)
    ctlPagination.text = paginationText
    toolbarPagination.text = paginationText
    currentDhikr = pagerAdapter.getDataAt(currentPage)
    updatePaginationAudio()
    updateBookmarkOptionIcon()
  }

  private fun updatePaginationAudio() {
    currentDhikr.audio?.let {
      audioUiManager.supported = true
      val path = audioManager.getAudioPath(it)
      val icon = if (audioManager.checkFileExists(path)) R.drawable.exo_controls_play else R.drawable.ic_file_download_white_24dp
      playFab.setImageResource(icon)
    } ?: run {
      audioUiManager.supported = false
    }
    audioUiManager.reset()
  }

  private fun updateBookmarkOptionIcon() {
    val menuItem = menu?.findItem(R.id.option_bookmark)
    if (bookmarkList.any { it.dhikrId == currentDhikr._id }) {
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white_24dp)
    } else {
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_white_24dp)
    }
  }

  private fun applyPlayerOptionsConfiguration() {
    listOf(playerCloseView, playerTransformView).forEach {
      it.outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
          val radius = resources.getDimension(R.dimen.margin_s)
          outline?.setRoundRect(0, 0, view?.width ?: 0, view?.height ?: 0, radius)
        }
      }
      it.clipToOutline = true
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

  inner class PlayListener : View.OnClickListener {
    override fun onClick(v: View?) {
      audioManager.play()
    }
  }

  inner class PlayerCloseListener : View.OnClickListener {
    override fun onClick(v: View?) {
      audioUiManager.close()
    }
  }

  inner class PlayerTransformListener : View.OnClickListener {
    override fun onClick(v: View?) {
      audioUiManager.transform()
    }
  }

}
