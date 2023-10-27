package com.arka.quotify.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arka.quotify.Handler.DatabaseHandler
import com.arka.quotify.HttpHandler.ApiInterface
import com.arka.quotify.HttpHandler.Retrofit_Instance
import com.arka.quotify.Model.SaveQuotes
import com.arka.quotify.R
import com.arka.quotify.RandomQuotesDataItem
import com.arka.quotify.UI.TAGHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    internal var quoteList = MutableLiveData<List<RandomQuotesDataItem>>()
    var currentImageResource: Int = R.drawable.baseline_bookmark_border_24
    var isImageChanged: Boolean = false
    var index = 0
    var ListSize = 0
    val quoteExists: LiveData<Boolean>
        get() = checkIfQuoteExistsLiveData

    private val checkIfQuoteExistsLiveData = MutableLiveData<Boolean>()

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
        Log.i("DatabaseInstance", database.toString())
        val quoteToSave = quoteList.value?.get(index)

        val existingQuote = database.SaveQuoteDAO().getQuoteByContent(quoteToSave?.content)
        existingQuote?.let { Log.i("DBSAVE", it.author.toString()) }
        if (existingQuote == null) {
            // Quote doesn't exist, insert it
            try {
                database.SaveQuoteDAO().insertQuote(
                    SaveQuotes(
                        0,
                        quoteList.value?.get(index)!!.content,
                        quoteList.value?.get(index)!!.author
                    )

                )
            } catch (e: Exception) {
                Log.i("DatabaseSaveError", e.toString())
            }


            Toast.makeText(
                applicationContext,
                "Quote saved to Favourite",
                Toast.LENGTH_SHORT
            ).show()
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

    suspend fun deleteAllQuotes(applicationContext: Context) {
        val database = DatabaseHandler.getDatabase(applicationContext)
        database.SaveQuoteDAO().deleteAllQuotes()
    }

    //
//    fun currentBookmarkStatus(): Boolean {
//
//        return isImageChanged
//    }
//
//    fun ChangeImageStatus() {
//        isImageChanged = !isImageChanged
//    }
//    suspend fun checkIfQuoteExists(quote: String, applicationContext: Context): Boolean {
//        val database = DatabaseHandler.getDatabase(applicationContext)
//
//        return database.SaveQuoteDAO().doesQuoteExist(quote)
//    }

    // Modify checkIfQuoteExists to set the value in LiveData
    suspend fun checkIfQuoteExists(quote: String, applicationContext: Context) {
        val database = DatabaseHandler.getDatabase(applicationContext)
        val quoteExists = database.SaveQuoteDAO().doesQuoteExist(quote)
        checkIfQuoteExistsLiveData.postValue(quoteExists)
    }

    suspend fun deleteQuoteByContent(quote: String, applicationContext: Context) {
        val database = DatabaseHandler.getDatabase(applicationContext)
        database.SaveQuoteDAO().deleteQuoteByContent(quote)
    }
}