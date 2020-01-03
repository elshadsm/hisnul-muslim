package com.elshadsm.muslim.hisnul.viewmodel.dhikrview

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.elshadsm.muslim.hisnul.R
import com.elshadsm.muslim.hisnul.activities.DhikrViewActivity
import com.elshadsm.muslim.hisnul.models.AUDIO_DIRECTORY
import com.elshadsm.muslim.hisnul.models.AUDIO_URL_PREFIX
import com.elshadsm.muslim.hisnul.services.LocalCacheDataSourceFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import kotlinx.android.synthetic.main.activity_dhikr_view.*
import java.io.File

class AudioManager(private val activity: DhikrViewActivity) {

  private var exoPlayer: SimpleExoPlayer? = null
  private var downloadId: Long = -1

  fun ensureExoPlayer() {
    if (exoPlayer == null) {
      initializeExoPlayer()
    }
  }

  fun initializeExoPlayer() {
    if (exoPlayer != null) return
    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
    activity.playerView.player = exoPlayer
    activity.playerCollapsedView.player = exoPlayer
  }

  fun releaseExoPlayer() {
    exoPlayer?.let {
      it.stop()
      it.release()
    }
    exoPlayer = null
  }

  fun onDownloadComplete(intent: Intent) {
    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
    if (downloadId == id) {
      activity.playFab.setImageResource(R.drawable.exo_controls_play)
    }
  }

  fun play() {
    activity.viewModel.currentDhikr?.audio?.let {
      val path = getAudioPath(it)
      if (checkFileExists(path)) {
        playAudio()
        activity.audioUiManager.open()
      } else {
        downloadAudio(it)
      }
    }
  }

  fun stop() {
    exoPlayer?.stop()
  }

  fun checkFileExists(path: String) = File(path).exists()

  fun getAudioPath(audio: String) =
      Environment.getExternalStoragePublicDirectory(AUDIO_DIRECTORY).toString() + File.separator + audio + ".mp3"

  private fun playAudio() {
    val mediaSource = buildMediaSource()
    exoPlayer?.apply {
      prepare(mediaSource)
      seekTo(0)
      playWhenReady = true
    }
  }

  private fun buildMediaSource(): MediaSource {
    val path = getAudioPath(activity.viewModel.currentDhikr?.audio ?: "")
    val mediaUri = Uri.parse(path)
    val dataSourceFactory = LocalCacheDataSourceFactory(activity)
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
    downloadId = (activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
  }

}
