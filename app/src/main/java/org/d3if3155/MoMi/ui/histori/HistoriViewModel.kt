package org.d3if3155.hitungbmi.ui.histori

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3155.MoMi.model.CategoryPic
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.MoMi.db.UserDao
import org.d3if3155.MoMi.db.UserEntity
import org.d3if3155.helloworld.network.CategoryPicApi
import org.d3if3155.helloworld.network.ApiStatus

class HistoriViewModel(
    private val db: TransactionDao,
    private val userDb: UserDao
) : ViewModel() {
    private val data = MutableLiveData<List<CategoryPic>>()
    private val status = MutableLiveData<ApiStatus>()

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

    private fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.postValue(ApiStatus.LOADING)

            try {
                data.postValue(CategoryPicApi.service.getCategoryPic())
                status.postValue(ApiStatus.SUCCESS)

            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.postValue(ApiStatus.FAILED)

            }
        }
    }
}