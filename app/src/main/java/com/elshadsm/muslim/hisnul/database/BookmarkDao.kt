package com.elshadsm.muslim.hisnul.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BookmarkDao {

  @Query("SELECT title._id as title_id, title.number as title_number , title.text as title, dhikr.number as dhikr_number FROM title INNER JOIN dhikr ON dhikr.bookmark = 1 and dhikr.title_id = title._id order by title.number, dhikr.number")
  fun getAll(): List<Bookmark>

}
