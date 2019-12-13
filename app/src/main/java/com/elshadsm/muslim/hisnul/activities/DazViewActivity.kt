package com.elshadsm.muslim.hisnul.activities

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.adapters.DazViewAdapter
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.models.*
import com.elshadsm.muslim.hisnul.services.GetDazFromDbTask
import com.elshadsm.muslim.hisnul.services.LocalCacheDataSourceFactory
import com.elshadsm.muslim.hisnul.services.PermissionsManager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import kotlinx.android.synthetic.main.activity_daz_view.*
import java.io.File
import java.lang.ref.WeakReference

class DazViewActivity : AppCompatActivity() {

  private val paginationStartNumber = 1
  private var downloadId: Long = -1
  private lateinit var currentDhikr: Dhikr

  private var menu: Menu? = null
  private var exoPlayer: SimpleExoPlayer? = null
  private val permissionsManager = PermissionsManager(this)
  private lateinit var pagerAdapter: DazViewAdapter
  private lateinit var onAudioComplete: BroadcastReceiver

  private var audioFeatureEnabled = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initializeUi()
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
      R.id.action_hide_or_display_play -> {
        hdeOrDisplayPlayAction()
        return true
      }
      R.id.action_bookmark -> return true
      R.id.action_share -> return true
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

  fun updateData(dazDataList: List<Dhikr>) {
    pagerAdapter.setData(dazDataList)
    viewPager.adapter = pagerAdapter
    updatePagination(paginationStartNumber, pagerAdapter.count)
  }

  fun hdeOrDisplayPlayAction() {
    val menuItem = menu?.findItem(R.id.action_hide_or_display_play)
    if (audioFeatureEnabled) {
      playFab.hide()
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_volume_up_white_24dp)
    } else {
      playFab.show()
      menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_volume_off_white_24dp)
    }
    audioFeatureEnabled = !audioFeatureEnabled
  }

  private fun initializeUi() {
    setContentView(R.layout.activity_daz_view)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration() {
    intent.extras?.getInt(DAZ_ID_EXTRA_NAME)?.let {
      GetDazFromDbTask(WeakReference(this), it).execute()
    }
    pagerAdapter = DazViewAdapter(supportFragmentManager, this)
    intent.extras?.getString(DAZ_TITLE_EXTRA_NAME)?.let {
      toolbarTitle.text = it
      ctlTitle.text = it
    }
    permissionsManager.start()
  }

  private fun registerEventHandlers() {
    appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener())
    viewPager.addOnPageChangeListener(SimpleOnPageChangeListener())
    onAudioComplete = DownloadCompleteBroadcastReceiver()
    registerReceiver(onAudioComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    playFab.setOnClickListener(PlayFabOnClickListener())
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
    ctlTitle.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    ctlPagination.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    toolbarTitle.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
    toolbarPagination.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
  }

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val pagination = String.format(resources.getString(R.string.daz_view_pagination), currentPage, totalPage)
    ctlPagination.text = pagination
    toolbarPagination.text = pagination
    currentDhikr = pagerAdapter.getDataAt(currentPage)
    currentDhikr.audio?.let {
      playFab.visibility = View.VISIBLE
      val path = getAudioPath(it)
      val icon = if (checkFileExists(path)) R.drawable.exo_controls_play else R.drawable.ic_file_download_white_24dp
      playFab.setImageResource(icon)
    } ?: run {
      playFab.visibility = View.INVISIBLE
    }
  }

  private fun initializeExoPlayer() {
    if (exoPlayer != null) {
      return
    }
    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
    audioPlayerView.player = exoPlayer
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
    val dataSourceFactory = LocalCacheDataSourceFactory(this)
    return ExtractorMediaSource
        .Factory(dataSourceFactory)
        .createMediaSource(mediaUri)
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
    override fun onPageSelected(position: Int) = updatePagination(position + 1, pagerAdapter.count)
  }

  inner class DownloadCompleteBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
      if (downloadId == id) {
        playFab.setImageResource(R.drawable.exo_controls_play)
      }
    }
  }

  inner class PlayFabOnClickListener : View.OnClickListener {
    override fun onClick(v: View?) {
      currentDhikr.audio?.let {
        val path = getAudioPath(it)
        if (checkFileExists(path)) {
          playAudio()
          audioPlayerView.visibility = View.VISIBLE
          playFab.visibility = View.INVISIBLE
        } else {
          downloadAudio(it)
        }
      }
    }
  }

}
