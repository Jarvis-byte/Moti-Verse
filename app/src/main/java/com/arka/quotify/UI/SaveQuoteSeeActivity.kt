package com.arka.quotify.UI


import android.graphics.Canvas
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arka.quotify.Adapter.QuoteSaveAdaptor
import com.arka.quotify.Handler.DatabaseHandler
import com.arka.quotify.Model.SaveQuotes
import com.arka.quotify.R
import com.arka.quotify.ViewModel.MainViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SaveQuoteSeeActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var FinalSaveQuoteList: LiveData<List<SaveQuotes>>
    lateinit var adapter: QuoteSaveAdaptor
    lateinit var back: ImageView
    lateinit var delete: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_quote_see)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val RVList = findViewById<RecyclerView>(R.id.RVList)
        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
        delete = findViewById(R.id.delete)
        delete.setOnClickListener {
            try {

                // Delete operation here
                CoroutineScope(Dispatchers.Main).launch {
                    mainViewModel.deleteAllQuotes(applicationContext)
                    Toast.makeText(
                        applicationContext,
                        "All Favourite Quotes Deleted Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mainViewModel.isImageChanged = false

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {

            FinalSaveQuoteList = mainViewModel.getQuote(applicationContext)
            adapter = QuoteSaveAdaptor(this@SaveQuoteSeeActivity, FinalSaveQuoteList)
            RVList.adapter = adapter
            RVList.layoutManager = LinearLayoutManager(this@SaveQuoteSeeActivity)

        }
        var itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder, target: ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    // move item in `fromPos` to `toPos` in adapter.
                    return true // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    // remove from adapter
                    val position = viewHolder.adapterPosition
                    val mutableList = FinalSaveQuoteList.value?.toMutableList() ?: mutableListOf()
                    val itemToRemove = mutableList.removeAt(position)

                    CoroutineScope(Dispatchers.IO).launch {
                        val database = DatabaseHandler.getDatabase(applicationContext)
                        database.SaveQuoteDAO().deleteQuoteByContent(itemToRemove.quote)
                    }

                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                this@SaveQuoteSeeActivity,
                                com.arka.quotify.R.color.my_background
                            )
                        )
                        .addActionIcon(com.arka.quotify.R.drawable.baseline_delete_24)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })
        itemTouchHelper.attachToRecyclerView(RVList)


    }


}