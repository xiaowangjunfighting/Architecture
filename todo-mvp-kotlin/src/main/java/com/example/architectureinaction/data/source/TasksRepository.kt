package com.example.architectureinaction.data.source

import com.example.architectureinaction.data.Task

/**
 * 管理内存，本地，网络三方的数据
 */
class TasksRepository(
    val localDataSource: TasksDataSource,
    val remoteDataSource: TasksDataSource
) : TasksDataSource {

    companion object {
        var INSTANCE: TasksRepository? = null

        @JvmStatic
        fun getIntance(local: TasksDataSource, remote: TasksDataSource): TasksRepository {
            return INSTANCE ?: TasksRepository(local, remote).apply {
                INSTANCE = this
            }
        }
    }

    //Map管理内存数据
    var cacheTasks = LinkedHashMap<String, Task>()

    //数据脏了，需要从网络获取
    var cacheIsDirty = false

    /**
     * 加载数据的策略：
     *
     *      1，若数据是脏的，则直接从网络下载：
     *          1.1，若网络上下载成功，则将数据存储到内存和数据库
     *          1.2，若网络上下载失败，则回调失败的接口方法onDataNotAvailable
 *          2，若数据不脏：
     *              2,1，若内存有数据，直接取
     *              2.2，若内存没有数据，从本地取数据
     *                  2.2.1，若取本地数据成功，将数据存储到内存；
     *                  2.2.2，若取本地取数据失败(或没有)，则从网络上取数据;
     */
    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {
        if (cacheIsDirty) {
            getRemoteTask(callback)
        } else {
            if (!cacheTasks.isEmpty()) {
                callback.onTaskLoaded(cacheTasks.values.toList())
                return
            }

            localDataSource.getTasks(object : TasksDataSource.LoadTaskCallback{
                override fun onTaskLoaded(tasks: List<Task>) {
                    //本地成功
                    saveTaskInMemory(tasks)
                    callback.onTaskLoaded(tasks)
                }

                override fun onDataNotAvailable() {
                    //本地是吧，则从网络取数据
                    getRemoteTask(callback)
                }
            })

        }
    }

    //这里不判断cacheIsDirty，逻辑与getTasks一样
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallBack) {
        val task = cacheTasks[taskId]
        if (task != null) {
            callback.onTaskLoaded(task)
            return
        }

        localDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallBack{
            override fun onTaskLoaded(task: Task) {
                cacheTasks[taskId] = task
                callback.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                remoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallBack{
                    override fun onTaskLoaded(task: Task) {
                        cacheTasks[taskId] = task
                        localDataSource.saveTask(task)
                        callback.onTaskLoaded(task)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    private fun getRemoteTask(callback: TasksDataSource.LoadTaskCallback) {
        remoteDataSource.getTasks(object : TasksDataSource.LoadTaskCallback {
            override fun onTaskLoaded(tasks: List<Task>) {
                //网络下载成功
                saveTaskInMemory(tasks)
                saveTaskInDisk(tasks)
                callback.onTaskLoaded(tasks) //数据传给Presenter
                cacheIsDirty = false
            }

            override fun onDataNotAvailable() {
                //网络失败
                callback.onDataNotAvailable()
            }
        })
    }

    fun saveTaskInMemory(tasks: List<Task>) {
        cacheTasks.clear()
        tasks.forEach {
            cacheTasks.put(it.id, it)
        }
    }

    fun saveTaskInDisk(tasks: List<Task>) {
        localDataSource.deleteAllTasks()
        tasks.forEach {
            localDataSource.saveTask(it)
        }
    }

    override fun saveTask(task: Task) {
        cacheAndPerform(task, task.isComplete) {
            localDataSource.saveTask(it)
            remoteDataSource.saveTask(it)
        }
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task, true) {
            localDataSource.completeTask(it)
            remoteDataSource.completeTask(it)
        }
    }

    /**
     * @param task old task instance
     * @param isComplete
     * @param perform perform localDataSource and remoteDataSource
     */
    private fun cacheAndPerform(task: Task, isComplete: Boolean, perform: (Task) -> Unit) {
        val cacheTask = Task(task.title, task.description, task.id).apply {
            this.isComplete = isComplete
            cacheTasks.put(id, this)
            perform(this)
        }

    }

    override fun complateTask(taskId: String) {
        cacheTasks[taskId]?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task, false) {
            localDataSource.activateTask(it)
            remoteDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskId: String) {
        cacheTasks[taskId]?.let {
            activateTask(it)
        }
    }

    override fun clearCompleteTasks() {
        cacheTasks = cacheTasks.filterValues { !it.isComplete } as LinkedHashMap<String, Task>
        localDataSource.clearCompleteTasks()
        remoteDataSource.clearCompleteTasks()
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        localDataSource.deleteAllTasks()
        remoteDataSource.deleteAllTasks()
        cacheTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        localDataSource.deleteTask(taskId)
        remoteDataSource.deleteTask(taskId)
        cacheTasks.remove(taskId)
    }

}