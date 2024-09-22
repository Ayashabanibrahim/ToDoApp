package com.example.todolistapp

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val readAllData:LiveData<List<Task>> = taskDao.readAllData()
    fun addTask(task: Task){
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: Task){
        taskDao.updateTask(task)
    }
    fun searchTask(query:String):LiveData<List<Task>>{
        return taskDao.searchTask(query)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }
    suspend fun deleteAllTasks(){
        taskDao.deleteAllTasks()
    }




}