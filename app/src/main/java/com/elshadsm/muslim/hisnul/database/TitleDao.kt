package com.elshadsm.muslim.hisnul.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TitleDao {

  @Insert
  fun insert(cities: List<Title>)

  @Query("SELECT * FROM Title")
  fun getAll(): List<Title>

}