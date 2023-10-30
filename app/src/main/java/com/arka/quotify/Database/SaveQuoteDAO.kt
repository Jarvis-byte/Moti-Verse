package com.arka.quotify.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arka.quotify.Model.SaveQuotes

@Dao
interface SaveQuoteDAO {
    @Insert
    suspend fun insertQuote(quotes: SaveQuotes)

    @Query("DELETE FROM SaveQuotes")
    suspend fun deleteAllQuotes()

    @Query("SELECT * FROM SaveQuotes")
    fun getSaveQuote(): LiveData<List<SaveQuotes>>

    @Query("SELECT * FROM SaveQuotes WHERE quote = :quote")
    suspend fun getQuoteByContent(quote: String?): SaveQuotes?

    @Query("SELECT EXISTS (SELECT 1 FROM SaveQuotes WHERE quote = :quote LIMIT 1)")
    suspend fun doesQuoteExist(quote: String): Boolean

    @Query("DELETE FROM SaveQuotes WHERE quote = :quote")
    suspend fun deleteQuoteByContent(quote: String)


}