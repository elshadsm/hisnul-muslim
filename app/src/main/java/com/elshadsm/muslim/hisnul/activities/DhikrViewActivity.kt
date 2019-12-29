package com.elshadsm.muslim.hisnul.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import kotlinx.android.synthetic.main.activity_dhikr_view.*
import java.io.File
import java.lang.ref.WeakReference

class DhikrViewActivity : AppCompatActivity() {

  private val paginationStartNumber = 0

  private var titleId: Int = 0
  private lateinit var currentDhikr: Dhikr
  private var bookmarkList: MutableList<Bookmark> = mutableListOf()

  var exoPlayer: SimpleExoPlayer? = null
  var menu: Menu? = null
  private val permissionsManager = PermissionsManager(this)
  private lateinit var pagerAdapter: DhikrViewAdapter
  private lateinit var onAudioComplete: BroadcastReceiver

  private var downloadId: Long = -1

  private lateinit var audioUiManager: AudioUiManager

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
    initializeExoPlayer()
  }

  override fun onResume() {
    super.onResume()
    if (exoPlayer == null) {
      initializeExoPlayer()
    }
  }

  override fun onStop() {
    super.onStop()
    if (audioUiManager.isOpen()) {
      audioUiManager.close()
    }
    releaseExoPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(onAudioComplete)
    releaseExoPlayer()
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
    audioUiManager = AudioUiManager(this)
  }

  private fun registerEventHandlers() {
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener())
    viewPager.addOnPageChangeListener(SimpleOnPageChangeListener())
    onAudioComplete = DownloadCompleteBroadcastReceiver()
    registerReceiver(onAudioComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    playFab.setOnClickListener(PlayFabListener())
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
      val path = getAudioPath(it)
      val icon = if (checkFileExists(path)) R.drawable.exo_controls_play else R.drawable.ic_file_download_white_24dp
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

  private fun initializeExoPlayer() {
    if (exoPlayer != null) return
    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
    playerView.player = exoPlayer
    playerCollapsedView.player = exoPlayer
  }

  private fun releaseExoPlayer() {
    exoPlayer?.let {
      it.stop()
      it.release()
    }
    exoPlayer = null
  }

  private fun checkFileExists(path: String) = File(path).exists()

  private fun getAudioPath(audio: String) =
      Environment.getExternalStoragePublicDirectory(AUDIO_DIRECTORY).toString() + File.separator + audio + ".mp3"

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

  inner class SimpleOnPageChangeListener : ViewPager.SimpleOnPageChangeListener() {
    override fun onPageSelected(position: Int) = updatePagination(position, pagerAdapter.count)
  }

  inner class DownloadCompleteBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
      if (downloadId == id) {
        playFab.setImageResource(R.drawable.exo_controls_play)
      }
    }

  }

  inner class PlayFabListener : View.OnClickListener {

    override fun onClick(v: View?) {
      currentDhikr.audio?.let {
        val path = getAudioPath(it)
        if (checkFileExists(path)) {
          playAudio()
          audioUiManager.open()
        } else {
          downloadAudio(it)
        }
      }
    }

    private fun playAudio() {
      val mediaSource = buildMediaSource()
      exoPlayer?.apply {
        prepare(mediaSource)
        seekTo(0)
        playWhenReady = true
      }
    }

    private fun buildMediaSource(): MediaSource {
      val path = getAudioPath(currentDhikr.audio ?: "")
      val mediaUri = Uri.parse(path)
      val dataSourceFactory = LocalCacheDataSourceFactory(this@DhikrViewActivity)
      return ExtractorMediaSource
          .Factory(dataSourceFactory)
          .createMediaSource(mediaUri)
    }

    private fun downloadAudio(audio: String) {
      val uri = Uri.parse("$AUDIO_URL_PREFIX$audio.mp3")
      val request = DownloadManager.Request(uri)
      request.setTitle("Downloading $audio.mp3")
      request.setDescription("Downloading $audio.mp3 file to the external public directory")
      request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
      request.setDestinationInExternalPublicDir("/$AUDIO_DIRECTORY", "$audio.mp3")
      request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
      request.setAllowedOverRoaming(false)
      request.setVisibleInDownloadsUi(true)
      downloadId = (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
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
