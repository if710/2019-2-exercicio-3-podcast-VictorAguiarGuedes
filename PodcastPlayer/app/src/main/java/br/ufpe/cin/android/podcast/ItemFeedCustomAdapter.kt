package br.ufpe.cin.android.podcast

import android.content.*
import android.net.Uri
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.itemlista.view.*
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.ufpe.cin.android.podcast.DownloadService.Companion.DOWNLOAD_COMPLETE


class ItemFeedCustomAdapter(private val itemsFeed: List<ItemFeed>, private val c: Context) : RecyclerView.Adapter<ItemFeedCustomAdapter.ViewHolder>() {

    internal var musicPlayerService: MusicPlayerWithBindingService? = null
    internal var isBound = false
    internal val TAG = "MusicBindingActivity"

    private val sConn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            musicPlayerService = null
            isBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, b: IBinder?) {
            val binder = b as MusicPlayerWithBindingService.MusicBinder
            musicPlayerService = binder.service
            isBound = true
        }
    }

    internal var downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("recebido", "RECEBEU")
            Log.e("recebido", intent.toString())
        }
    }

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

        if (!isBound) {
            val bindIntent = Intent(c, MusicPlayerWithBindingService::class.java)
            isBound = c.bindService(bindIntent,sConn, Context.BIND_AUTO_CREATE)
        }

        val musicServiceIntent = Intent(c, MusicPlayerWithBindingService::class.java)
        c.startService(musicServiceIntent)

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

        holder.itemAction.setOnClickListener{
            val downloadService = Intent(c, DownloadService::class.java)
            downloadService.data = Uri.parse(itemFeed.title)
            c.startService(downloadService)
            LocalBroadcastManager.getInstance(c).registerReceiver(downloadReceiver, IntentFilter(DOWNLOAD_COMPLETE))
        }

        holder.botaoPlay.setOnClickListener {
            if (isBound) {
                musicPlayerService?.playMusic()
            }
        }
        holder.botaoPause.setOnClickListener {
            if (isBound) {
                musicPlayerService?.pauseMusic()
            }
        }
    }

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val itemTitle = row.item_title
        val itemAction = row.item_action
        val itemDate = row.item_date
        val botaoPlay = row.botaoPlay
        val botaoPause = row.botaoPause
    }

}