package com.elshadsm.muslim.hisnul.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "dhikr", foreignKeys = [
  ForeignKey(entity = Title::class, parentColumns = ["_id"], childColumns = ["title_id"])])
class Dhikr(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val arabic: String,
    val compiled: String,
    val translation: String,
    val reference: String,
    @ColumnInfo(name = "title_id")
    val titleId: Int)
