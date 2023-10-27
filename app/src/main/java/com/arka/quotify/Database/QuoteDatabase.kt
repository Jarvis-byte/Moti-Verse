package com.arka.quotify.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arka.quotify.Model.SaveQuotes

@Database(entities = [SaveQuotes::class], version = 1)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun SaveQuoteDAO(): SaveQuoteDAO


}