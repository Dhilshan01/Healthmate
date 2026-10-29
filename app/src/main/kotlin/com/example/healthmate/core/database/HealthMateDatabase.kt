package com.example.healthmate.core.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Owns the on-device HealthMate SQLite database and its schema migrations.
 * Keep DB_NAME stable so existing user data remains available across app updates.
 */
class HealthMateDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createHealthRecords(db)
        createDailyGoals(db)
        createDailyReminders(db)
        createUserProfile(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Apply migrations in order so an install can safely upgrade across versions.
        if (oldVersion < 2) {
            createUserProfile(db)
        }
    }

    private fun createHealthRecords(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE health_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                steps INTEGER NOT NULL,
                calories INTEGER NOT NULL,
                water INTEGER NOT NULL
            )""".trimIndent()
        )
    }

    private fun createDailyGoals(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE daily_goals (
                id INTEGER PRIMARY KEY,
                steps INTEGER NOT NULL,
                calories INTEGER NOT NULL,
                water INTEGER NOT NULL
            )""".trimIndent()
        )
    }

    private fun createDailyReminders(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE daily_reminders (
                id INTEGER PRIMARY KEY,
                waterEnabled INTEGER NOT NULL,
                waterMinutes INTEGER NOT NULL,
                walkingEnabled INTEGER NOT NULL,
                walkingMinutes INTEGER NOT NULL,
                activityEnabled INTEGER NOT NULL,
                activityMinutes INTEGER NOT NULL
            )""".trimIndent()
        )
    }

    private fun createUserProfile(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS user_profile (
                id INTEGER PRIMARY KEY,
                age INTEGER,
                heightCm REAL,
                weightKg REAL,
                activityLevel TEXT NOT NULL,
                useMetricUnits INTEGER NOT NULL
            )""".trimIndent()
        )
    }

    companion object {
        private const val DB_NAME = "healthmate.db"
        private const val DATABASE_VERSION = 2

        @Volatile
        private var instance: HealthMateDatabase? = null

        fun getInstance(context: Context): HealthMateDatabase =
            instance ?: synchronized(this) {
                instance ?: HealthMateDatabase(context).also { instance = it }
            }
    }
}
