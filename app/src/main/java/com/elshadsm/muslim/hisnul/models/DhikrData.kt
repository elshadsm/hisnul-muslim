package com.elshadsm.muslim.hisnul.models

import android.os.Parcel
import android.os.Parcelable

class DhikrData(
    var title: String? = "",
    var arabic: String? = "",
    var compiled: String? = "",
    var translation: String? = "",
    var reference: String? = "") : Parcelable {

  constructor(parcel: Parcel) : this() {
    title = parcel.readString()
    arabic = parcel.readString()
    compiled = parcel.readString()
    translation = parcel.readString()
    reference = parcel.readString()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
    parcel.writeString(arabic)
    parcel.writeString(compiled)
    parcel.writeString(translation)
    parcel.writeString(reference)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DhikrData> {
    override fun createFromParcel(parcel: Parcel): DhikrData {
      return DhikrData(parcel)
    }

    override fun newArray(size: Int): Array<DhikrData?> {
      return arrayOfNulls(size)
    }
  }
}