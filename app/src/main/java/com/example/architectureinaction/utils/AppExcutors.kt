package com.example.architectureinaction.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val THREAD_COUNT = 3

/**
 * 封装线程池代码;
 * SAM可以用lambda替换;
 */
class AppExcutors(
    val diskIO: Executor = DiskIOThreadPool(),
    val networkIO: Executor = Executors.newFixedThreadPool(THREAD_COUNT),
    val mainThread: Executor = MainThreadExecutor()) {

    private class DiskIOThreadPool : Executor {
        val diskIO = Executors.newSingleThreadExecutor()

        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }

    private class MainThreadExecutor : Executor {
        val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}

