package com.elshadsm.muslim.hisnul.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "title")
data class Title(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val number: Int,
    val text: String)
