package br.ufpe.cin.android.podcast

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities= arrayOf(ItemFeed::class), version=1)
abstract class ItemsFeedDB : RoomDatabase() {
    abstract fun itemsFeedDAO() : ItemsFeedDAO
    companion object {
        private var INSTANCE : ItemsFeedDB? = null
        fun getDatabase(ctx : Context) : ItemsFeedDB {
            if (INSTANCE == null) {
                synchronized(ItemsFeedDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        ctx.applicationContext,
                        ItemsFeedDB::class.java,
                        "itemsfeed.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}