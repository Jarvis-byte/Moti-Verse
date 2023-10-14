package com.example.quotify.Model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "SaveQuotes")

data class SaveQuotes(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val quote: String,
    val author: String
)