package ru.anydevprojects.simplepodcastapp.core.dataStore.models

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import ru.anydevprojects.simplepodcastapp.UserPrefs

object UserPrefsSerializer : Serializer<UserPrefs> {
    override val defaultValue: UserPrefs = UserPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefs {
        return UserPrefs.parseFrom(input)
    }

    override suspend fun writeTo(t: UserPrefs, output: OutputStream) {
        t.writeTo(output)
    }
}
