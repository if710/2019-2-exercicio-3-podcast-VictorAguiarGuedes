package br.ufpe.cin.android.podcast

import androidx.room.*

@Dao
interface ItemsFeedDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserirItem(vararg itemFeed:ItemFeed)
    @Update
    fun atualizarItem(vararg itemFeed:ItemFeed)
    @Delete
    fun removerItem(vararg itemFeed:ItemFeed)
    @Query("SELECT * FROM itemsFeed")
    fun todosItems() : List<ItemFeed>
    @Query("SELECT * FROM itemsFeed WHERE pubDate LIKE :q")
    fun buscaItemPelaData(q : String) : List<ItemFeed>
}