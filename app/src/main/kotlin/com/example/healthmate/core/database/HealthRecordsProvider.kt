package com.example.healthmate.core.database

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.content.UriMatcher

/** Read-only ContentProvider API for querying health records and observing changes. */
class HealthRecordsProvider : ContentProvider() {
    private lateinit var database: HealthMateDatabase

    override fun onCreate(): Boolean {
        val appContext = context ?: return false
        database = HealthMateDatabase.getInstance(appContext)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        require(MATCHER.match(uri) == RECORDS) { "Unsupported URI: $uri" }
        val cursor = SQLiteQueryBuilder().apply {
            tables = "health_records"
        }.query(
            database.readableDatabase,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder ?: "date DESC, id DESC"
        )
        cursor.setNotificationUri(requireNotNull(context).contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String = when (MATCHER.match(uri)) {
        RECORDS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.health_record"
        else -> throw IllegalArgumentException("Unsupported URI: $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri =
        throw UnsupportedOperationException("Health records are read-only through this provider")

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException("Health records are read-only through this provider")

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException("Health records are read-only through this provider")

    companion object {
        const val AUTHORITY = "com.example.healthmate.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/records")

        private const val RECORDS = 1
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "records", RECORDS)
        }
    }
}
