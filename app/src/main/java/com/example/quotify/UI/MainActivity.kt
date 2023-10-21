package com.example.quotify.UI


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.quotify.Handler.DeviceIdHandler
import com.example.quotify.R
import com.example.quotify.RandomQuotesDataItem
import com.example.quotify.ViewModel.MainViewModel
import com.example.quotify.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val TAGHttp = "Http Call"


class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    private var isImageChanged = false

    lateinit var floatingSaveButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.loadingAnimation.playAnimation()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.quote = mainViewModel.getQuote()

        mainViewModel.getQuotelist().observe(this, Observer {
            binding.loadingAnimation.visibility = View.GONE
            binding.quoteText.visibility = View.VISIBLE
            binding.quoteAuthor.visibility = View.VISIBLE
            binding.quoteImage.visibility = View.VISIBLE
            setQuote(mainViewModel.getQuote())

        })

        mainViewModel.apicall()

        GlobalScope.launch(Dispatchers.Main) {
            val Token = getToken()
            if (Token != null) {
                Log.i("NEW_TOKEN", Token)
                val tokenData = mapOf("token" to Token)
                val DeviceId = DeviceIdHandler.getToken()
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("DeviceToken").document(DeviceId).set(tokenData)

            } else {
                // Handle the error
                Log.e("TOKEN_ERROR", "Failed to retrieve FCM token")
            }
        }

        floatingSaveButton = findViewById<ImageButton>(R.id.floatingSaveButton)

//        updateImageButton()
    }

    fun setQuote(quote: RandomQuotesDataItem?) {

        binding.quoteText.text = quote?.content
        binding.quoteAuthor.text = quote?.author

    }

    fun onShare(view: View) {
        val shareText = "${binding.quoteText.text}\n\n— ${binding.quoteAuthor.text}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)


    }

    fun onSave(view: View) {
        if (view is ImageButton) {
            mainViewModel.isImageChanged = !mainViewModel.isImageChanged
            Log.i("STATE", mainViewModel.isImageChanged.toString())
            updateImageButton()
        }
    }

    private fun updateImageButton() {
        if (mainViewModel.isImageChanged) {
            floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_24)
            saveQuote(applicationContext)
        } else {
            floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_border_24)

            // Delete the saved data
        }
    }


    fun onNext(view: View) {
        mainViewModel.nextQuote()
//        mainViewModel.isImageChanged = !mainViewModel.isImageChanged
        CoroutineScope(Dispatchers.Main).launch {
            val quoteExists = mainViewModel.checkIfQuoteExists(
                mainViewModel.getQuote()!!.content,
                applicationContext
            )
            if (quoteExists) {
                // The quote exists in the database
                mainViewModel.isImageChanged =true
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                // The quote does not exist in the database
                mainViewModel.isImageChanged =false
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_border_24)
            }

        }

        setQuote(mainViewModel.getQuote())
//        Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show()


    }

    fun onPrevious(view: View) {
        mainViewModel.PrevQuote()

        CoroutineScope(Dispatchers.Main).launch {
            val quoteExists = mainViewModel.checkIfQuoteExists(
                mainViewModel.getQuote()!!.content,
                applicationContext
            )
            if (quoteExists) {
                // The quote exists in the database
                mainViewModel.isImageChanged =true
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                // The quote does not exist in the database
                mainViewModel.isImageChanged =false
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_border_24)
            }

        }
        setQuote(mainViewModel.getQuote())


    }

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val task = FirebaseMessaging.getInstance().token.await()
                task
            } catch (e: Exception) {
                null
            }
        }
    }

    fun saveQuote(applicationContext: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.saveQuote(applicationContext)
        }
    }

    fun onSaveView(view: View) {
        val intent = Intent(this, SaveQuoteSeeActivity::class.java)
        startActivity(intent)
    }

}


