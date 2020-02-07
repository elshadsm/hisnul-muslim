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
class Dhikr(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val arabic: String,
    val compiled: String,
    val translation: String,
    val reference: String,
    val audio: String?,
    var bookmark: Int,
    var number: Int,
    @ColumnInfo(name = "title_id")
    val titleId: Int) : Parcelable {
  var bookmarked: Boolean
    set(value) {
      bookmark = if (value) 1 else 0
    }
    get() = bookmark == 1
}
