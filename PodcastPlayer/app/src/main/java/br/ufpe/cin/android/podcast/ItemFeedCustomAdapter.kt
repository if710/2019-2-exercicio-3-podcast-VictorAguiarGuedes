package br.ufpe.cin.android.podcast

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.itemlista.view.*
import android.util.Log


class ItemFeedCustomAdapter(private val itemsFeed: List<ItemFeed>, private val c: Context) : RecyclerView.Adapter<ItemFeedCustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(c).inflate(R.layout.itemlista, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemsFeed.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemFeed = itemsFeed[position]
        holder.itemTitle.text = itemFeed.title
        holder.itemDate.text = itemFeed.pubDate

        holder.itemTitle.setOnClickListener{
            val intent = Intent(c, EpisodeDetailActivity::class.java)
            intent.putExtra("title",itemFeed.title)
            intent.putExtra("pubDate",itemFeed.pubDate)
            intent.putExtra("description",itemFeed.description)
            c.startActivity(intent)
        }

        holder.itemAction.setOnClickListener {
            Log.e("Link de Download", itemFeed.downloadLink)
        }
    }

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val itemTitle = row.item_title
        val itemAction = row.item_action
        val itemDate = row.item_date
    }

}