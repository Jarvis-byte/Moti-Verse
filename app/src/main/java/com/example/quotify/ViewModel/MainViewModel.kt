package com.example.quotify.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quotify.Handler.DatabaseHandler
import com.example.quotify.HttpHandler.ApiInterface
import com.example.quotify.HttpHandler.Retrofit_Instance
import com.example.quotify.Model.SaveQuotes
import com.example.quotify.RandomQuotesDataItem
import com.example.quotify.UI.TAGHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    internal var quoteList = MutableLiveData<List<RandomQuotesDataItem>>()


    var index = 0
    var ListSize = 0

    // Getter for quoteList
    internal fun getQuotelist(): MutableLiveData<List<RandomQuotesDataItem>> {
        return quoteList
    }


    fun apicall() {
        if (quoteList.value?.isEmpty() == false) {
            return
        }
        val quotesApi = Retrofit_Instance.getInstance().create(ApiInterface::class.java)
        Log.i(TAGHttp, "Called Again")
        CoroutineScope(Dispatchers.IO).launch {
            val call = quotesApi.getRandomQuotes(20)
            if (call.body() != null) {
                quoteList.postValue(call.body()!!)
                ListSize = call.body()!!.size
            }
        }
    }

    internal fun getQuote(): RandomQuotesDataItem? {
        return quoteList.value?.get(index)
    }

    fun nextQuote() {
        if (ListSize > 0) {
            index = (index + 1) % ListSize

        }
    }

    fun PrevQuote() {
        if (ListSize > 0) {
            index = (index - 1 + ListSize) % ListSize

        }
    }

    suspend fun saveQuote(applicationContext: Context) {
        val database = DatabaseHandler.getDatabase(applicationContext)
        val quoteToSave = quoteList.value?.get(index)
        val existingQuote = database.SaveQuoteDAO().getQuoteByContent(quoteToSave?.content)
        if (existingQuote == null) {
            // Quote doesn't exist, insert it
            database.SaveQuoteDAO().insertQuote(
                SaveQuotes(
                    0,
                    quoteList.value?.get(index)!!.content,
                    quoteList.value?.get(index)!!.author
                )

            )
            Toast.makeText(applicationContext, "Quote Saved to wishlist", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Already Saved", Toast.LENGTH_SHORT).show()
            // Quote already exists, handle it as per your requirements
            // You can show a message, log it, or take any other action.
        }


    }

    fun getQuote(applicationContext: Context): LiveData<List<SaveQuotes>> {
        val database = DatabaseHandler.getDatabase(applicationContext)
        val quoteListInDb = database.SaveQuoteDAO().getSaveQuote()
        return quoteListInDb
    }

    fun deleteQuote(applicationContext: Context) {
        val database = DatabaseHandler.getDatabase(applicationContext)

        // Observe the LiveData and perform the delete operation when data is available
        database.SaveQuoteDAO().getSaveQuote().observeForever { quotesList ->
            quotesList?.let { nonNullQuotesList ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.SaveQuoteDAO().deleteQuote(nonNullQuotesList)
                }
            }
        }

    }
}