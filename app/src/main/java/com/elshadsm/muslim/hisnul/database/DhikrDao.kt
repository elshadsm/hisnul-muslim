package com.elshadsm.muslim.hisnul.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface DhikrDao {

  @Query("SELECT * from dhikr where title_id = :titleId")
  fun getDhikr(titleId: Int): List<Dhikr>

  @Query("SELECT * from dhikr where bookmark = 1")
  fun getAllBookmarkedDhikrList(): List<Dhikr>

  @Query("UPDATE dhikr SET bookmark = :bookmark WHERE _id = :id")
  fun updateBookmark(id: Int, bookmark: Int)

}