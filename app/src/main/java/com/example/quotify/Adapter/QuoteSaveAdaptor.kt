package com.example.quotify.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quotify.Model.SaveQuotes
import com.example.quotify.R

class QuoteSaveAdaptor(val context: Context, val quote: LiveData<List<SaveQuotes>>) :
    RecyclerView.Adapter<QuoteViewHolder>() {

    private var quoteList: List<SaveQuotes> = emptyList()

    init {
        // Observe the LiveData and update the local list when it changes
        quote.observeForever { quotesList ->
            quoteList = quotesList ?: emptyList()
            notifyDataSetChanged() // Notify the adapter that the data has changed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return QuoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quoteList.size
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quoteList[position]
        holder.quote.text = quote.quote
        holder.author.text = quote.author
    }


}

class QuoteViewHolder(itemView: View) : ViewHolder(itemView) {
    var quote = itemView.findViewById<TextView>(R.id.Quote_in_RV)
    var author = itemView.findViewById<TextView>(R.id.Author_in_RV)
}
