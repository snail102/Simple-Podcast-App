package ru.anydevprojects.simplepodcastapp.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.PodcastFeedDao
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.SubscriptionPodcastFeedDao
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.PodcastFeedEntity
import ru.anydevprojects.simplepodcastapp.podcastFeed.data.models.SubscriptionPodcastFeedEntity

@Database(entities = [SubscriptionPodcastFeedEntity::class, PodcastFeedEntity::class], version = 1)
abstract class PodcastDatabase : RoomDatabase() {
    abstract fun getSubscriptionPodcastFeedDao(): SubscriptionPodcastFeedDao
    abstract fun getPodcastFeedDao(): PodcastFeedDao
}

fun createDataBase(applicationContext: Context): PodcastDatabase = Room.databaseBuilder(
    applicationContext,
    PodcastDatabase::class.java,
    "podcast_database"
).build()
