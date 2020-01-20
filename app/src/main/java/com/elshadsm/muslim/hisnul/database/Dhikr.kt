package com.elshadsm.muslim.hisnul.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "dhikr", foreignKeys = [
  ForeignKey(entity = Title::class, parentColumns = ["_id"], childColumns = ["title_id"])
])
data class Dhikr(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val arabic: String,
    val compiled: String,
    val translation: String,
    val reference: String,
    val audio: String?,
    @ColumnInfo(name = "title_id")
    val titleId: Int) : Parcelable
