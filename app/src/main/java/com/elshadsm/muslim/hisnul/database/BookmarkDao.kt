package com.elshadsm.muslim.hisnul.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {

  @Query("SELECT * from bookmark where title_id = :titleId")
  fun getBookmark(titleId: Int): MutableList<Bookmark>

  @Insert
  fun insertBookmark(bookmark: Bookmark): Long

  @Delete
  fun deleteBookmark(bookmark: Bookmark)

}
