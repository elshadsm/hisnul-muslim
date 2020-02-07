package com.elshadsm.muslim.hisnul.database

import androidx.room.ColumnInfo

data class Bookmark(
    @ColumnInfo(name = "title_id")
    var titleId: Int,
    @ColumnInfo(name = "title_number")
    var titleNumber: Int,
    var title: String,
    @ColumnInfo(name = "dhikr_id")
    var dhikrId: Int,
    @ColumnInfo(name = "dhikr_number")
    var dhikrNumber: Int)