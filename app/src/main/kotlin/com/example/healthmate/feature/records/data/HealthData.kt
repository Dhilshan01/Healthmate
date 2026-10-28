package com.example.healthmate.feature.records.data

import android.content.*
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HealthRecord(val id: Long = 0, val date: String, val steps: Int, val calories: Int, val water: Int)
data class DailyGoals(val id: Int = 1, val steps: Int = 10_000, val calories: Int = 500, val water: Int = 2_500)
data class ReminderSettings(val id: Int = 1, val waterEnabled: Boolean = false, val waterMinutes: Int = 600, val walkingEnabled: Boolean = false, val walkingMinutes: Int = 1020, val activityEnabled: Boolean = false, val activityMinutes: Int = 1200)
data class UserProfile(
    val id: Int = 1,
    val age: Int? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val activityLevel: String = "Moderately active",
    val useMetricUnits: Boolean = true
)

private class HealthDatabase(context: Context) : SQLiteOpenHelper(context, "healthmate.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) { db.execSQL("CREATE TABLE health_records (id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, steps INTEGER NOT NULL, calories INTEGER NOT NULL, water INTEGER NOT NULL)"); db.execSQL("CREATE TABLE daily_goals (id INTEGER PRIMARY KEY, steps INTEGER NOT NULL, calories INTEGER NOT NULL, water INTEGER NOT NULL)"); db.execSQL("CREATE TABLE daily_reminders (id INTEGER PRIMARY KEY, waterEnabled INTEGER NOT NULL, waterMinutes INTEGER NOT NULL, walkingEnabled INTEGER NOT NULL, walkingMinutes INTEGER NOT NULL, activityEnabled INTEGER NOT NULL, activityMinutes INTEGER NOT NULL)"); createProfileTable(db) }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { if (oldVersion < 2) createProfileTable(db) }
    private fun createProfileTable(db: SQLiteDatabase) { db.execSQL("CREATE TABLE IF NOT EXISTS user_profile (id INTEGER PRIMARY KEY, age INTEGER, heightCm REAL, weightKg REAL, activityLevel TEXT NOT NULL, useMetricUnits INTEGER NOT NULL)") }
}

class HealthRepository(context: Context) {
    private val database = HealthDatabase(context.applicationContext)
    private val _records = MutableStateFlow(readRecords()); val records: StateFlow<List<HealthRecord>> = _records
    private val _goals = MutableStateFlow(readGoals()); val goals: StateFlow<DailyGoals> = _goals
    private val _reminders = MutableStateFlow(readReminders()); val reminders: StateFlow<ReminderSettings> = _reminders
    private val _profile = MutableStateFlow(readProfile()); val profile: StateFlow<UserProfile> = _profile
    private fun readRecords(): List<HealthRecord> = database.readableDatabase.rawQuery("SELECT * FROM health_records ORDER BY date DESC, id DESC", null).use { c -> buildList { while (c.moveToNext()) add(HealthRecord(c.getLong(0), c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4))) } }
    private fun readGoals(): DailyGoals = database.readableDatabase.rawQuery("SELECT * FROM daily_goals WHERE id=1", null).use { c -> if (c.moveToFirst()) DailyGoals(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3)) else DailyGoals() }
    private fun readReminders(): ReminderSettings = database.readableDatabase.rawQuery("SELECT * FROM daily_reminders WHERE id=1", null).use { c -> if (c.moveToFirst()) ReminderSettings(c.getInt(0), c.getInt(1) == 1, c.getInt(2), c.getInt(3) == 1, c.getInt(4), c.getInt(5) == 1, c.getInt(6)) else ReminderSettings() }
    private fun readProfile(): UserProfile = database.readableDatabase.rawQuery("SELECT * FROM user_profile WHERE id=1", null).use { c -> if (c.moveToFirst()) UserProfile(c.getInt(0), if (c.isNull(1)) null else c.getInt(1), if (c.isNull(2)) null else c.getDouble(2), if (c.isNull(3)) null else c.getDouble(3), c.getString(4), c.getInt(5) == 1) else UserProfile() }
    suspend fun add(r: HealthRecord) { database.writableDatabase.insert("health_records", null, ContentValues().apply { put("date", r.date); put("steps", r.steps); put("calories", r.calories); put("water", r.water) }); _records.value = readRecords() }
    suspend fun update(r: HealthRecord) { database.writableDatabase.update("health_records", ContentValues().apply { put("date", r.date); put("steps", r.steps); put("calories", r.calories); put("water", r.water) }, "id=?", arrayOf(r.id.toString())); _records.value = readRecords() }
    suspend fun delete(r: HealthRecord) { database.writableDatabase.delete("health_records", "id=?", arrayOf(r.id.toString())); _records.value = readRecords() }
    suspend fun saveGoals(g: DailyGoals) { database.writableDatabase.insertWithOnConflict("daily_goals", null, ContentValues().apply { put("id", 1); put("steps", g.steps); put("calories", g.calories); put("water", g.water) }, SQLiteDatabase.CONFLICT_REPLACE); _goals.value = g }
    suspend fun saveReminders(r: ReminderSettings) { database.writableDatabase.insertWithOnConflict("daily_reminders", null, ContentValues().apply { put("id", 1); put("waterEnabled", if(r.waterEnabled) 1 else 0); put("waterMinutes", r.waterMinutes); put("walkingEnabled", if(r.walkingEnabled) 1 else 0); put("walkingMinutes", r.walkingMinutes); put("activityEnabled", if(r.activityEnabled) 1 else 0); put("activityMinutes", r.activityMinutes) }, SQLiteDatabase.CONFLICT_REPLACE); _reminders.value = r }
    suspend fun saveProfile(p: UserProfile) { database.writableDatabase.insertWithOnConflict("user_profile", null, ContentValues().apply { put("id", 1); if (p.age == null) putNull("age") else put("age", p.age); if (p.heightCm == null) putNull("heightCm") else put("heightCm", p.heightCm); if (p.weightKg == null) putNull("weightKg") else put("weightKg", p.weightKg); put("activityLevel", p.activityLevel); put("useMetricUnits", if (p.useMetricUnits) 1 else 0) }, SQLiteDatabase.CONFLICT_REPLACE); _profile.value = p }
}
