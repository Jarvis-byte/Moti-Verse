package com.example.quotify.UI

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotify.Adapter.QuoteSaveAdaptor
import com.example.quotify.Model.SaveQuotes
import com.example.quotify.R
import com.example.quotify.ViewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveQuoteSeeActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var FinalSaveQuoteList: LiveData<List<SaveQuotes>>
    lateinit var adapter: QuoteSaveAdaptor
    lateinit var back: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_quote_see)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val RVList = findViewById<RecyclerView>(R.id.RVList)
        back = findViewById(R.id.back)
        back.setOnClickListener {
                finish()
        }

        try {
            // Delete operation here
            mainViewModel.deleteQuote(applicationContext)
            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        CoroutineScope(Dispatchers.Main).launch {

            FinalSaveQuoteList = mainViewModel.getQuote(applicationContext)
            adapter = QuoteSaveAdaptor(this@SaveQuoteSeeActivity, FinalSaveQuoteList)
            RVList.adapter = adapter
            RVList.layoutManager = LinearLayoutManager(this@SaveQuoteSeeActivity)

        }
    }
}