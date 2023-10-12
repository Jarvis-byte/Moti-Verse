package com.example.quotify.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quotify.HttpHandler.ApiInterface
import com.example.quotify.HttpHandler.Retrofit_Instance
import com.example.quotify.RandomQuotesDataItem
import com.example.quotify.UI.TAGHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    var quoteList = MutableLiveData<List<RandomQuotesDataItem>>()
    var index = 0
    var ListSize = 0

    // Getter for quoteList
    fun getQuotelist(): MutableLiveData<List<RandomQuotesDataItem>> {
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

    fun getQuote(): RandomQuotesDataItem? {
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
}