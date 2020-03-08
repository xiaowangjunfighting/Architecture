package com.example.architectureinaction.data.source.remote

import com.example.architectureinaction.data.Task
import com.example.architectureinaction.data.source.TasksDataSource

/**
 * 网络请求数据的类，暂时用内存管理来模拟
 */
class TaskRemoteDataSource private constructor(): TasksDataSource {

    private val TASKS_SERVICE_DATA = LinkedHashMap<String, Task>()

    companion object {
        private var INSTANCE: TaskRemoteDataSource? = null

        @JvmStatic
        fun getIntance() : TaskRemoteDataSource{
            if (INSTANCE == null) {
                synchronized(TaskRemoteDataSource::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = TaskRemoteDataSource()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {
        //子线程loading data..

        //这里是模拟，暂时不需要切换到主线程
        if (TASKS_SERVICE_DATA.isEmpty()) { //模拟网络下载失败的条件
            callback.onDataNotAvailable()
        } else {
            callback.onTaskLoaded(TASKS_SERVICE_DATA.values.toList())
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallBack) {
        val task = TASKS_SERVICE_DATA[taskId] //模拟网络请求，这里原本需要异步

        //这里原本需要切换到主线程
        if (task == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onTaskLoaded(task)
        }
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun completeTask(task: Task) {
        task.isComplete = true
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun complateTask(taskId: String) {

    }

    override fun activateTask(task: Task) {
        task.isComplete = false
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun activateTask(taskId: String) {
    }

    override fun clearCompleteTasks() {
        TASKS_SERVICE_DATA.entries.iterator().apply {
            while (hasNext()) {
                if (next().value.isComplete) {
                    remove()
                }
            }
        }
    }

    override fun refreshTasks() {

    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}