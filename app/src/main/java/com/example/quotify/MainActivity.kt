package com.example.quotify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quotify.ViewModel.MainViewModel

const val TAGHttp = "Http Call"

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val quoteText: TextView = findViewById(R.id.quoteText)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.getQuotelist().observe(this, Observer {

            quoteText.text = it.get(1).content

        })
        mainViewModel.apicall()


    }


    fun onShare(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        mainViewModel.getQuotelist().observe(this, Observer {
            intent.putExtra(Intent.EXTRA_TEXT, it.get(1).content)

        })

        startActivity(intent)
    }

    fun onNext(view: View) {}
    fun onPrevious(view: View) {}

}


