package org.d3if3155.MoMi.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.MoMi.db.UserDao
import org.d3if3155.MoMi.db.UserEntity

class TransactionViewModel(
    private val db: TransactionDao,
    private val userDb: UserDao
) : ViewModel() {
    val currentUser = MutableLiveData<UserEntity>()
    val currentUserAmount = this.currentUser.switchMap {
        db.getTotalAmount(it.id)
    }

    fun getAllTransactions(): LiveData<List<TransactionEntity>> {
        val allTransactions = this.currentUser.switchMap {
            db.getAllTransaction(it.id)
        }
        return allTransactions
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            db.insert(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            db.delete(transaction)
        }
    }

    fun addOrSubtractAmount(userId: Long, amount: Long, type: Boolean, imageId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.insert(
                    TransactionEntity(
                        userId = userId,
                        amount = amount,
                        type = type,
                        imageId = imageId
                    )
                )
            }
        }
    }

    fun getUser(userId: Long): LiveData<UserEntity> {
        return userDb.getUser(userId)
    }
}
