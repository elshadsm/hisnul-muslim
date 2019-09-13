package com.elshadsm.muslim.hisnul.services

import android.content.Context
import com.elshadsm.muslim.hisnul.R
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

class LocalCacheDataSourceFactory(private val context: Context) : DataSource.Factory {

  private val defaultDataSourceFactory: DefaultDataSourceFactory
  private val simpleCache: SimpleCache = SimpleCacheFactory.getInstance(this.context)
  private val cacheDataSink: CacheDataSink = CacheDataSink(simpleCache, SimpleCacheFactory.MAX_FILE_SIZE)
  private val fileDataSource: FileDataSource = FileDataSource()

  init {
    val userAgent = Util.getUserAgent(this.context, this.context.getString(R.string.app_name))
    val bandwidthMeter = DefaultBandwidthMeter()
    defaultDataSourceFactory = DefaultDataSourceFactory(
        this.context,
        bandwidthMeter,
        DefaultHttpDataSourceFactory(userAgent)
    )
  }

  override fun createDataSource(): DataSource {
    return CacheDataSource(
        simpleCache, defaultDataSourceFactory.createDataSource(),
        fileDataSource, cacheDataSink,
        CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
    )
  }

}

object SimpleCacheFactory {

  const val MAX_FILE_SIZE: Long = 5 * 1024 * 1024

  private const val MAX_CACHE_SIZE: Long = 100 * 1024 * 1024
  private var instance: SimpleCache? = null

  fun getInstance(context: Context): SimpleCache = instance ?: SimpleCache(
      File(context.cacheDir, "media"),
      LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
  ).also { instance = it }

}
