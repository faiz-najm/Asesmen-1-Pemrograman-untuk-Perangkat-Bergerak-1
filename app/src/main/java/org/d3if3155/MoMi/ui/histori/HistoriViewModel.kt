package org.d3if3155.hitungbmi.ui.histori

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.MoMi.db.UserDao
import org.d3if3155.MoMi.db.UserEntity

class HistoriViewModel(
    private val db: TransactionDao,
    private val userDb: UserDao
) : ViewModel() {
    val currentUser = MutableLiveData<UserEntity>()
    val userTransaction = this.currentUser.switchMap {
        db.getAllTransaction(it.id)
    }

    fun hapusAllData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            db.clearData()
        }
    }

    fun getUser(userId: Long): LiveData<UserEntity> {
        return userDb.getUser(userId)
    }

    fun hapusTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.delete(transaction)
            }
        }
    }
}