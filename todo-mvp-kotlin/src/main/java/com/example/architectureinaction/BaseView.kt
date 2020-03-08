package com.example.architectureinaction

interface BaseView<T> {
    //注入Presenter
    var presenter: T
}