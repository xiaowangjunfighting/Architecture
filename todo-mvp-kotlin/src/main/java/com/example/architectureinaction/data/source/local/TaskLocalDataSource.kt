package com.example.architectureinaction.data.source.local

import com.example.architectureinaction.data.Task
import com.example.architectureinaction.data.source.TasksDataSource
import com.example.architectureinaction.utils.AppExcutors

/**
 * 本地数据库处理
 */
class TaskLocalDataSource private constructor(
    val excutors: AppExcutors,
    val dao: TasksDao
) : TasksDataSource {

    companion object {
        private var INSTANCE: TaskLocalDataSource? = null

        @JvmStatic
        fun getInstance(excutors: AppExcutors, dao: TasksDao) : TaskLocalDataSource{
            if (INSTANCE == null) {
                synchronized(TaskLocalDataSource::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = TaskLocalDataSource(excutors, dao)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {
        excutors.diskIO.execute {
            val tasks = dao.getTasks()

            //切换到主线程，后面需要更新UI
            excutors.mainThread.execute {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTaskLoaded(tasks)
                }
            }
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallBack) {
        excutors.diskIO.execute {
            val task = dao.getTaskById(taskId)

            excutors.mainThread.execute{
                if (task == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTaskLoaded(task)
                }
            }
        }

    }

    override fun saveTask(task: Task) {
        excutors.diskIO.execute {
            dao.insertTask(task)
        }
    }

    override fun completeTask(task: Task) {
        excutors.diskIO.execute {
            dao.updateCompleted(task.id, true)
        }
    }

    override fun complateTask(taskId: String) {

    }

    override fun activateTask(task: Task) {
        excutors.diskIO.execute{dao.updateCompleted(task.id, false)}
    }

    override fun activateTask(taskId: String) {
    }

    override fun clearCompleteTasks() {
        excutors.diskIO.execute{dao.deleteCompletedTask()}
    }

    override fun refreshTasks() {

    }

    override fun deleteAllTasks() {excutors.diskIO.execute{dao.deleteTasks()}}

    override fun deleteTask(taskId: String) {excutors.diskIO.execute{dao.deleteTaskById(taskId)}}
}