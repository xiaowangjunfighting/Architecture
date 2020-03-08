package com.example.architectureinaction.data.source

import com.example.architectureinaction.data.Task

interface TasksDataSource {

    fun saveTask(task: Task)

    fun completeTask(task: Task)

    fun complateTask(taskId: String)

    fun activateTask(task: Task)

    fun activateTask(taskId: String)

    fun clearCompleteTasks()

    fun refreshTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: String)

    fun getTasks(callback: LoadTaskCallback)

    fun getTask(taskId: String, callback: GetTaskCallBack)

    /**
     * 下载task数据，提供两种状态
     */
    interface LoadTaskCallback {
        fun onTaskLoaded(tasks: List<Task>)
        fun onDataNotAvailable()

    }
    interface GetTaskCallBack {
        fun onTaskLoaded(task: Task)
        fun onDataNotAvailable()

    }
}