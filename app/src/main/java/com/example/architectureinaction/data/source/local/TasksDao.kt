package com.example.architectureinaction.data.source.local

import androidx.room.*
import com.example.architectureinaction.data.Task

@Dao
interface TasksDao {

    @Query(value = "select * from task") fun getTasks(): List<Task>

    @Query(value = "select * from task where entryid = :entryid") fun getTaskById(entryid: String) : Task?

    @Update fun updateTask(task: Task) : Int  //返回更新的任务个数

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertTask(task: Task)

    @Query("update task set complete = :completed where entryid = :taskid")
    fun updateCompleted(taskid: String, completed: Boolean)

    @Query("delete from task") fun deleteTasks()

    @Query("delete from task where entryid = :taskid")
    fun deleteTaskById(taskid: String)

    @Query("delete from task where complete = 1")
    fun deleteCompletedTask() : Int //返回删除的任务个数

}