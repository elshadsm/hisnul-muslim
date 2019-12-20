package com.elshadsm.muslim.hisnul.database

import android.os.Parcel
import android.os.Parcelable
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
    val audio: String?,
    @ColumnInfo(name = "title_id")
    val titleId: Int) : Parcelable {

  constructor(parcel: Parcel) : this(
      parcel.readInt(),
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString(),
      parcel.readInt())

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(_id)
    parcel.writeString(arabic)
    parcel.writeString(compiled)
    parcel.writeString(translation)
    parcel.writeString(reference)
    parcel.writeString(audio)
    parcel.writeInt(titleId)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Dhikr> {
    override fun createFromParcel(parcel: Parcel): Dhikr {
      return Dhikr(parcel)
    }

    override fun newArray(size: Int): Array<Dhikr?> {
      return arrayOfNulls(size)
    }
  }

}
