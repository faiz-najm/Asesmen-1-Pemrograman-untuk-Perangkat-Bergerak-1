package org.d3if3155.MoMi.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: TransactionEntity)

    @Update
    fun update(transaction: TransactionEntity)

    @Delete
    fun delete(transaction: TransactionEntity)

    // get transaction from 1 user only
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransaction(userId: Long): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransaction(id: Int): LiveData<TransactionEntity>

    // Total income subtract total expense from 1 user only (userId)
    @Query("SELECT SUM(CASE WHEN type = 1 THEN amount ELSE 0 END) - SUM(CASE WHEN type = 0 THEN amount ELSE 0 END) FROM transactions WHERE userId = :userId")
    fun getTotalAmount(userId: Long): LiveData<Int>

    @Query("DELETE FROM transactions")
    fun clearData()

}