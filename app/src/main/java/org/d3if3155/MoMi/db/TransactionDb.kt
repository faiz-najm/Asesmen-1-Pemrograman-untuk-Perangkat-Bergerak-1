package org.d3if3155.MoMi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class TransactionDb : RoomDatabase() {

    abstract val transactionDao: TransactionDao
    abstract val userDao: UserDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDb? = null

        fun getInstance(context: Context): TransactionDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDb::class.java,
                    "transaction.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}