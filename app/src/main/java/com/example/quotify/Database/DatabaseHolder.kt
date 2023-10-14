package com.example.quotify.Database

import android.content.Context
import androidx.room.Room

object DatabaseHolder {
    private var database: QuoteDatabase? = null

    fun getDatabase(context: Context): QuoteDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                QuoteDatabase::class.java,
                "QuoteDb"
            ).build().also { database = it }
        }
    }
}