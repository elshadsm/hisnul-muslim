package com.elshadsm.muslim.hisnul.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TitleDao {

  @Insert
  fun insert(cities: List<Title>)

  @Query("SELECT * FROM title")
  fun getAll(): List<Title>

  @Query("SELECT * FROM title where _id = :id")
  fun getTitle(id: Int): Title

  @Query("SELECT * FROM title WHERE text LIKE '%' || :query || '%'")
  fun search(query: String): List<Title>

}
