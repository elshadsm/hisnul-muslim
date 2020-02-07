package com.elshadsm.muslim.hisnul.listeners

class GeneralListenersManager {

  companion object {

    interface BookmarkListener {
      fun onBookmarkUpdate()
    }

    private val bookmarkListenerList = mutableListOf<BookmarkListener>()

    fun addBookmarkListener(listener: BookmarkListener) = bookmarkListenerList.add(listener)

    fun notifyBookmarkUpdated() {
      println(" notifyBookmarkUpdated notifyBookmarkUpdated notifyBookmarkUpdated ${bookmarkListenerList.size}")
      bookmarkListenerList.forEach { it.onBookmarkUpdate() }
    }
  }

}
