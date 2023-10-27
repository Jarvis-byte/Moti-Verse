package com.arka.quotify.Handler

import android.content.Context
import androidx.room.Room
import com.arka.quotify.Database.QuoteDatabase

object DatabaseHandler {
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