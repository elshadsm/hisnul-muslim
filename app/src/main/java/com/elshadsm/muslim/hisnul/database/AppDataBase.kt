package com.elshadsm.muslim.hisnul.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.FileOutputStream
import java.io.IOException

@Database(entities = [Title::class, Dhikr::class, Bookmark::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

  abstract fun titleDao(): TitleDao
  abstract fun dhikrDao(): DhikrDao
  abstract fun bookmarkDao(): BookmarkDao

  companion object {

    private val LOG_TAG = AppDataBase::class.java.simpleName
    private const val DATABASE_NAME: String = "hisnul.sqlite3"

    @JvmField
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
      override fun migrate(database: SupportSQLiteDatabase) {
      }
    }

    @Volatile
    private var INSTANCE: AppDataBase? = null

    fun getInstance(context: Context): AppDataBase {
      synchronized(this) {
        var instance = INSTANCE
        if (instance == null) {
          instance = createInstance(context)
          INSTANCE = instance
        }
        return instance
      }
    }

    private fun createInstance(context: Context): AppDataBase {
      copyAttachedDatabase(context)
      return Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, DATABASE_NAME)
          .addMigrations(MIGRATION_1_2)
          .build()
    }

    private fun copyAttachedDatabase(context: Context) {
      val dbPath = context.getDatabasePath(DATABASE_NAME)
      if (dbPath.exists()) return
      dbPath.parentFile.mkdirs()
      try {
        val inputStream = context.assets.open("databases/$DATABASE_NAME")
        val output = FileOutputStream(dbPath)
        val buffer = ByteArray(8192)
        var length: Int
        while (true) {
          length = inputStream.read(buffer)
          if (length <= 0) break
          output.write(buffer, 0, length)
        }
        output.flush()
        output.close()
        inputStream.close()
      } catch (exception: IOException) {
        Log.d(LOG_TAG, "Failed to open file", exception)
        exception.printStackTrace()
      }
    }

  }

}
