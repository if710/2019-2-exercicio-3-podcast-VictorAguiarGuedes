package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_episode_detail.*

class EpisodeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        item_feed_title.text = intent.getStringExtra("title")
        item_feed_pubDate.text = intent.getStringExtra("pubDate")
        item_feed_description.text = intent.getStringExtra("description")

    }
}
