package com.elshadsm.muslim.hisnul.models

import android.os.Parcel
import android.os.Parcelable

class DazData(
    var title: String? = "",
    var arabic: String? = "",
    var compiled: String? = "",
    var translation: String? = "",
    var reference: String? = "",
    var pagination: String? = "") : Parcelable {

  constructor(parcel: Parcel) : this() {
    title = parcel.readString()
    arabic = parcel.readString()
    compiled = parcel.readString()
    translation = parcel.readString()
    reference = parcel.readString()
    pagination = parcel.readString()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
    parcel.writeString(arabic)
    parcel.writeString(compiled)
    parcel.writeString(translation)
    parcel.writeString(reference)
    parcel.writeString(pagination)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DazData> {
    override fun createFromParcel(parcel: Parcel): DazData {
      return DazData(parcel)
    }

    override fun newArray(size: Int): Array<DazData?> {
      return arrayOfNulls(size)
    }
  }
}