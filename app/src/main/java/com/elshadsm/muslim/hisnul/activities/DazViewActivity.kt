package com.elshadsm.muslim.hisnul.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import androidx.viewpager.widget.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.adapters.DazViewAdapter
import com.elshadsm.muslim.hisnul.database.Dhikr
import com.elshadsm.muslim.hisnul.models.*
import com.elshadsm.muslim.hisnul.services.GetDazFromDbTask
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_daz_view.*
import java.lang.ref.WeakReference

class DazViewActivity : AppCompatActivity() {

  private val paginationStartNumber = 1

  private var menu: Menu? = null
  private var exoPlayer: SimpleExoPlayer? = null
  private var audioLastPosition: Long = 0
  private var audioPlayed: Boolean = false
  private lateinit var pagerAdapter: DazViewAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initializeUi()
    applyConfiguration(savedInstanceState)
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

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putLong(PLAYER_POSITION_KEY, audioLastPosition)
    outState.putBoolean(PLAYER_STATUS_KEY, audioPlayed)
    super.onSaveInstanceState(outState)
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

  override fun onPause() {
    super.onPause()
    exoPlayer?.let {
      audioLastPosition = it.currentPosition
    }
  }

  override fun onStop() {
    super.onStop()
    releaseExoPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    releaseExoPlayer()
  }

  fun updateData(dazDataList: List<Dhikr>) {
    pagerAdapter.setData(dazDataList)
    viewPager.adapter = pagerAdapter
    updatePagination(paginationStartNumber, pagerAdapter.count)
  }

  private fun initializeUi() {
    setContentView(R.layout.activity_daz_view)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  private fun applyConfiguration(savedInstanceState: Bundle?) {
    intent.extras?.getInt(DAZ_ID_EXTRA_NAME)?.let {
      GetDazFromDbTask(WeakReference(this), it).execute()
    }
    pagerAdapter = DazViewAdapter(supportFragmentManager)
    intent.extras?.getString(DAZ_TITLE_EXTRA_NAME)?.let {
      toolbarTitle.text = it
      ctlTitle.text = it
    }
    applyExoPlayerConfiguration(savedInstanceState)
  }

  private fun applyExoPlayerConfiguration(savedInstanceState: Bundle?) {
    audioLastPosition = C.TIME_UNSET
    audioPlayed = true
    savedInstanceState?.let {
      audioLastPosition = it.getLong(PLAYER_POSITION_KEY, C.TIME_UNSET)
      audioPlayed = it.getBoolean(PLAYER_STATUS_KEY, true)
    }
  }

  private fun registerEventHandlers() {
    appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
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
    viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
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
    ctlTitle.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    ctlPagination.visibility = if (toolbarCollapsed) View.INVISIBLE else View.VISIBLE
    toolbarTitle.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
    toolbarPagination.visibility = if (toolbarCollapsed) View.VISIBLE else View.INVISIBLE
  }

  private fun updatePagination(currentPage: Int, totalPage: Int) {
    val pagination = String.format(resources.getString(R.string.daz_view_pagination), currentPage, totalPage)
    ctlPagination.text = pagination
    toolbarPagination.text = pagination
  }

  private fun initializeExoPlayer() {
    if (exoPlayer != null) {
      return
    }
    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
    audioPlayerView.player = exoPlayer
    val mediaSource = buildMediaSource()
    exoPlayer?.apply {
      prepare(mediaSource)
      seekTo(audioLastPosition)
      playWhenReady = audioPlayed
    }
  }

  private fun buildMediaSource(): MediaSource {
    val mediaUri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
    val dataSourceFactory = DefaultDataSourceFactory(this,
        Util.getUserAgent(this, getString(R.string.app_name)))
    return ExtractorMediaSource
        .Factory(dataSourceFactory)
        .createMediaSource(mediaUri)
  }

  private fun releaseExoPlayer() {
    exoPlayer?.let {
      audioPlayed = it.playWhenReady
      it.stop()
      it.release()
    }
    exoPlayer = null
  }

}
