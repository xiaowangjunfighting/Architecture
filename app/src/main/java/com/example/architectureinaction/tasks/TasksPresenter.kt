package com.example.architectureinaction.tasks

import com.example.architectureinaction.data.source.TasksRepository

//通过主构造方法向Presenter中注入Fragment
class TasksPresenter(val tasksView: TasksContract.View, val repo: TasksRepository) : TasksContract.Presenter {

    var filterType = TasksFilterType.ALL_TASKS

    init {
        //向Fragment中注入Presenter
        tasksView.presenter = this
    }

    override fun start() {

    }
}