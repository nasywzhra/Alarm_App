package com.nasywa.myalarmapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDao {
    @Insert
    fun AddAlarm (alarm: Alarm)

    @Query("SELECT * FROM alarm")

    fun getAlarm(): LiveData<List<Alarm>>

    @Delete
    fun deleteAlarm(alarm: Alarm)
}