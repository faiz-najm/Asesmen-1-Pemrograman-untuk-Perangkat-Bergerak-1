package org.d3if3155.MoMi.ui.transaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.db.TransactionEntity

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao: TransactionDao
    private val allTransactions: LiveData<List<TransactionEntity>> // Expose as public property

    init {
        val database = TransactionDb.getInstance(application)
        transactionDao = database.dao
        allTransactions = transactionDao.getAllTransaction()
    }

    fun getAllTransactions(): LiveData<List<TransactionEntity>> {
        return allTransactions
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.insert(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.delete(transaction)
        }
    }

    /*import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao: TransactionDao
    val allTransactions: LiveData<List<Transaction>> // Expose as public property

    init {
        val database = TransactionDatabase.getDatabase(application)
        transactionDao = database.transactionDao()
        allTransactions = transactionDao.getAllTransactions().asLiveData()
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(transaction)
        }
    }
}*/
}
