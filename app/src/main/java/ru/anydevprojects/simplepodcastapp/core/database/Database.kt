// package ru.anydevprojects.simplepodcastapp.core.database
//
// import android.content.Context
// import androidx.room.Database
// import androidx.room.Room
// import androidx.room.RoomDatabase
//
// @Database(entities = [], version = 1)
// abstract class PodcastDatabase : RoomDatabase()
//
// fun createDataBase(applicationContext: Context): PodcastDatabase = Room.databaseBuilder(
//    applicationContext,
//    PodcastDatabase::class.java,
//    "podcast_database"
// ).fallbackToDestructiveMigration().build()
