package com.example.quotify.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quotify.HttpHandler.ApiInterface
import com.example.quotify.HttpHandler.ResultRandom
import com.example.quotify.HttpHandler.Retrofit_Instance
import com.example.quotify.TAGHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var quoteList = MutableLiveData<List<ResultRandom>>()
    private var index = 0

    // Getter for quoteList
    fun getQuotelist(): MutableLiveData<List<ResultRandom>> {
        return quoteList
    }

    // Function to update quoteList
//    fun updateQuoteList(newList: LiveData<List<ResultRandom>>?) {
//        if (newList != null) {
//            quoteList = newList
//        }
//    }

    fun apicall() {
        if (quoteList.value?.isEmpty() == false) {
            return
        }
        val quotesApi = Retrofit_Instance.getInstance().create(ApiInterface::class.java)
        Log.i(TAGHttp, "Called Again")
        CoroutineScope(Dispatchers.IO).launch {
            val call = quotesApi.getListQuotes(1)
            if (call.body() != null) {
                quoteList.postValue(call.body()!!.results)
            }

        }
    }
}