package com.example.architectureinaction.tasks

import com.example.architectureinaction.BasePresenter
import com.example.architectureinaction.BaseView

interface TasksContract {
    interface Presenter : BasePresenter {

    }

    interface View : BaseView<Presenter> {

    }
}