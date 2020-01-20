package com.elshadsm.muslim.hisnul.database

import androidx.room.*

@Entity(tableName = "bookmark", foreignKeys = [
  ForeignKey(entity = Title::class, parentColumns = ["_id"], childColumns = ["title_id"]),
  ForeignKey(entity = Dhikr::class, parentColumns = ["_id"], childColumns = ["dhikr_id"])
])
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    @ColumnInfo(name = "title_id")
    val titleId: Int,
    @ColumnInfo(name = "dhikr_id")
    val dhikrId: Int)