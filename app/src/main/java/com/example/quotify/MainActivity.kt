package com.example.quotify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quotify.HttpHandler.ResultRandom
import com.example.quotify.ViewModel.MainViewModel

const val TAGHttp = "Http Call"

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var quoteText: TextView
    lateinit var quoteAuthor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        quoteText = findViewById(R.id.quoteText)
        quoteAuthor = findViewById(R.id.quoteAuthor)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel.getQuotelist().observe(this, Observer {
            setQuote(mainViewModel.getQuote())

        })
        mainViewModel.apicall()


    }

    fun setQuote(quote: ResultRandom?) {
        quoteText.text = quote?.content
        quoteAuthor.text = quote?.author

    }

    fun onShare(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        mainViewModel.getQuotelist().observe(this, Observer {
            intent.putExtra(Intent.EXTRA_TEXT, it.get(0).content)

        })

        startActivity(intent)
    }

    fun onNext(view: View) {

        mainViewModel.nextQuote()
        setQuote(mainViewModel.getQuote())
//        Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show()


    }

    fun onPrevious(view: View) {
        mainViewModel.nextQuote()
        setQuote(mainViewModel.getQuote())


    }

}


