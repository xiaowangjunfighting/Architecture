package com.example.architectureinaction.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task")
data class Task(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString()
) {

    val titleForList = if (title.isNotEmpty()) title else description

    @ColumnInfo(name = "complete") var isComplete = false

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()

    val isActive
        get() = !isComplete
}