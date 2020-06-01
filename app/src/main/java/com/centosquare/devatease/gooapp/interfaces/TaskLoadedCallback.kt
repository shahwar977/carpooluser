package com.centosquare.devatease.gooapp.interfaces

interface TaskLoadedCallback {

    fun onTaskDone(vararg values: Any)
    fun onTimeDone(vararg value: String)
}