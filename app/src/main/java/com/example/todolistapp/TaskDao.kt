package com.example.todolistapp
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTask(task: Task)

    @Query("SELECT * FROM taskTable ORDER BY ID ASC")
    fun readAllData():LiveData<List<Task>>

    @Query("SELECT * FROM taskTable WHERE title LIKE '%' || :query || '%'")
    fun searchTask(query: String?):LiveData<List<Task>>

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM taskTable WHERE done=1")
    suspend fun deleteAllTasks()

    @Update
    suspend fun updateTask(task: Task)


}