package com.arka.quotify.UI


import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arka.quotify.Handler.DeviceIdHandler
import com.arka.quotify.HttpHandler.NoInternet
import com.arka.quotify.R
import com.arka.quotify.RandomQuotesDataItem
import com.arka.quotify.ViewModel.MainViewModel
import com.arka.quotify.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val TAGHttp = "Http Call"


class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var floatingSaveButton: ImageButton
    var firsttime: Boolean = true
    var alertDialog: AlertDialog? = null
    private var tokenJob: Job? = null

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 100

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        No Internet
        val noInternet = NoInternet()
        noInternet.isConnected(applicationContext)
        if (!noInternet.isConnected(applicationContext)) {
            showCustomeDialog()
            return
        }
        /*
        No Internet finish
        */
        binding.loadingAnimation.playAnimation()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.quote = mainViewModel.getQuote()

        mainViewModel.getQuotelist().observe(this, Observer {

            binding.quoteText.visibility = View.VISIBLE
            binding.quoteAuthor.visibility = View.VISIBLE
            binding.quoteImage.visibility = View.VISIBLE
            setQuote(mainViewModel.getQuote())
            binding.loadingAnimation.visibility = View.GONE

        })
        binding.quoteText.setTextIsSelectable(true)
        binding.quoteAuthor.setTextIsSelectable(true)

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

        checkPermission(
            Manifest.permission.POST_NOTIFICATIONS,
            NOTIFICATION_PERMISSION_CODE
        )
//        updateImageButton()
//        checkDbToGetStatus()
        floatingSaveButton.setImageResource(mainViewModel.currentImageResource)
        mainViewModel.quoteExists.observe(this) { quoteExists ->
            if (quoteExists) {
                // The quote exists in the database
                mainViewModel.isImageChanged = true
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                // The quote does not exist in the database
                mainViewModel.isImageChanged = false
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
        }


    }

    private fun copyToClipboard(quoteText: String, quoteAuthor: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", "❝ $quoteText ❞ \n🎙️ — $quoteAuthor")
        clipboardManager.setPrimaryClip(clipData)
    }

    fun onCopy(view: View) {

//        val textToCopy = "${binding.quoteText.text} - by ${binding.quoteAuthor.text}"

        val textToCopy = binding.quoteText.text.toString()
        val authorToCopy = binding.quoteAuthor.text.toString()
        copyToClipboard(textToCopy, authorToCopy)
    }

    override fun onRestart() {
        super.onRestart()
        checkDbToGetStatus()

    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {

        }
    }

    private fun showCustomeDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_no_internet, null)
        builder.setView(dialogView)
        alertDialog = builder.create()
        // Set the dialog to not be cancelable by clicking outside of it
        alertDialog!!.setCanceledOnTouchOutside(false)
        dialogView.findViewById<View>(R.id.btnReset).setOnClickListener { // aLodingDialog.show();
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            alertDialog!!.dismiss()
        }
        dialogView.findViewById<View>(R.id.Cancel).setOnClickListener {

            alertDialog!!.dismiss()
            finish()


        }
        if (alertDialog!!.window != null) {
            alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog!!.show()
    }

    // This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Notification Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Notification Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    suspend fun deleteQuote(content: String, applicationContext: Context) {
        val job = CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.deleteQuoteByContent(content, applicationContext)
        }
        job.join()
        Toast.makeText(applicationContext, "Quote removed from Favourite", Toast.LENGTH_SHORT)
            .show()

    }

    fun setQuote(quote: RandomQuotesDataItem?) {

        binding.quoteText.text = quote?.content
        binding.quoteAuthor.text = quote?.author

    }

    fun onShare(view: View) {
        val shareText = "❝${binding.quoteText.text}❞\n\n 🎤 — ${binding.quoteAuthor.text}"

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
            mainViewModel.currentImageResource = R.drawable.baseline_bookmark_24
            saveQuote(applicationContext)
            firsttime = false
        } else {
            mainViewModel.currentImageResource = R.drawable.baseline_bookmark_border_24
            if (!firsttime) {
                CoroutineScope(Dispatchers.Main).launch {
                    deleteQuote(mainViewModel.getQuote()!!.content, applicationContext)
                }
            }
            firsttime = false
        }

        // Set the image resource from the ViewModel
        floatingSaveButton.setImageResource(mainViewModel.currentImageResource)
    }


    fun onNext(view: View) {
        mainViewModel.nextQuote()

        checkDbToGetStatus()
        setQuote(mainViewModel.getQuote())

//        Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show()


    }

    fun checkDbToGetStatus() {
        mainViewModel.quoteExists.observe(this) { quoteExists ->
            if (quoteExists) {
                // The quote exists in the database
                mainViewModel.isImageChanged = true
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_24)
            } else {
                // The quote does not exist in the database
                mainViewModel.isImageChanged = false
                floatingSaveButton.setImageResource(R.drawable.baseline_bookmark_border_24)
            }
        }

        // Trigger the checkIfQuoteExists function
        CoroutineScope(Dispatchers.Main).launch {
            mainViewModel.checkIfQuoteExists(
                mainViewModel.getQuote()!!.content,
                applicationContext
            )
        }
    }


    fun onPrevious(view: View) {
        mainViewModel.PrevQuote()

        checkDbToGetStatus()

        setQuote(mainViewModel.getQuote())
    }

    suspend fun getToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                token
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

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.getQuotelist().removeObservers(this)
        mainViewModel.quoteExists.removeObservers(this)
        alertDialog?.dismiss()
        tokenJob?.cancel()
    }

}


