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
//            Log.i("List",mainViewModel.getQuote().)
            setQuote(mainViewModel.getQuote())

        })
        mainViewModel.apicall()


    }

    fun setQuote(quote: ResultRandom?) {
        quoteText.text = quote?.content
//        Log.i(TAGHttp+"Content", quote?.content.toString())
//        Log.i(TAGHttp+"Author", quote?.author.toString())
        quoteAuthor.text = quote?.author

    }

    fun onShare(view: View) {
        val shareText = "${quoteText.text}\n\n— ${quoteAuthor.text}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }


    fun onNext(view: View) {
        mainViewModel.nextQuote()
        setQuote(mainViewModel.getQuote())
//        Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show()


    }

    fun onPrevious(view: View) {
        mainViewModel.PrevQuote()
        setQuote(mainViewModel.getQuote())


    }

}


