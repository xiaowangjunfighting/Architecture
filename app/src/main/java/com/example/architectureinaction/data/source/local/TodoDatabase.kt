package com.example.architectureinaction.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.architectureinaction.data.Task

@Database(entities = [Task::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TasksDao

    companion object {
        private val lock = Any()
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context) : TodoDatabase {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java, "Tasks.db").build()

                    }
                }
            }
            return INSTANCE!!
        }
    }
}