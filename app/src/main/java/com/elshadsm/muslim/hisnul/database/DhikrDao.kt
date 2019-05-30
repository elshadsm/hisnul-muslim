package com.elshadsm.muslim.hisnul.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DhikrDao {

  @Query("SELECT * from dhikr where title_id = :titleId")
  fun getDhikr(titleId: Int): List<Dhikr>

}