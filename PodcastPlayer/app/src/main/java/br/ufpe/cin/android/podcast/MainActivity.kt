package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.net.URL
import androidx.recyclerview.widget.LinearLayoutManager
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ItemsFeedDB.getDatabase(this@MainActivity)
        doAsync {
            var itemFeedList : List<ItemFeed>? = null
            try {
                //Lendo o conteúdo do XML armazenado na URL
                val xmlText = URL("https://s3-us-west-1.amazonaws.com/podcasts.thepolyglotdeveloper.com/podcast.xml").readText()
                itemFeedList = Parser.parse(xmlText)

                //Inserindo cada elemento do XML no banco de dados
                for(i in itemFeedList) {
                    db.itemsFeedDAO().inserirItem(i)
                }
            } catch (e: InterruptedException) {
                Log.e("Erro", "Erro ao carregar XML")
            } finally {
                //Carregando os arquivos armazenados no banco de dados
                //Assim, caso não haja conexão com internet, o app ainda carrega os últimos arquivos armazenados
                //E no caso de possuir conexão, como anteriormente cada item foi salvo no banco de dados, e com configuração
                //de replace no caso de conflito, o banco já vai estar com os dados atualizados
                itemFeedList = db.itemsFeedDAO().todosItems()

                uiThread {
                    //Setando um LinearLayout para organizar a estrutura dos elementos do RecyclerView
                    listRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    //Definindo o adapter
                    listRecyclerView.adapter = ItemFeedCustomAdapter(itemFeedList!!, this@MainActivity)
                    //Dividers entre os itens
                    listRecyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
                }
            }
        }
    }

}
