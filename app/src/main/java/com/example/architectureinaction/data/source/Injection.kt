package com.example.architectureinaction.data.source

import android.content.Context
import com.example.architectureinaction.data.source.local.TaskLocalDataSource
import com.example.architectureinaction.data.source.local.TodoDatabase
import com.example.architectureinaction.data.source.remote.TaskRemoteDataSource
import com.example.architectureinaction.utils.AppExcutors

object Injection {

    fun provideTaskRepository(context: Context): TasksRepository {
        return with(TodoDatabase.getInstance(context).taskDao()){
            TasksRepository(
                TaskLocalDataSource.getInstance(AppExcutors(), this),
                TaskRemoteDataSource.getIntance())
        }
    }
}