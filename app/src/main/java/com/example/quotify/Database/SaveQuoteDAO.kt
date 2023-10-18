package com.example.quotify.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.quotify.Model.SaveQuotes

@Dao
interface SaveQuoteDAO {
    @Insert
    suspend fun insertQuote(quotes: SaveQuotes)

    @Delete
    suspend fun deleteQuote(quotes:List<SaveQuotes>?)

    @Query("SELECT * FROM SaveQuotes")
    fun getSaveQuote(): LiveData<List<SaveQuotes>>

    @Query("SELECT * FROM SaveQuotes WHERE quote = :quote")
    suspend fun getQuoteByContent(quote: String?): SaveQuotes?


}