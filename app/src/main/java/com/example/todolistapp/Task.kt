package com.example.todolistapp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

//@Parcelize
@Entity(tableName="taskTable")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @NotNull
    @ColumnInfo
    val title:String,
    @ColumnInfo
    val description:String,
    @ColumnInfo
    val time:String,
    @ColumnInfo
    var done:Boolean=false
)
