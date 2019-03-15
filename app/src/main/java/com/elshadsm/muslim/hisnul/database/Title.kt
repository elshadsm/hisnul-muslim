package com.elshadsm.muslim.hisnul.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "title")
class Title(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val index: Int,
    val text: String)
