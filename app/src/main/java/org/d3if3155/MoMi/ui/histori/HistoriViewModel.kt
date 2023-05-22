package org.d3if3155.hitungbmi.ui.histori

import android.service.autofill.UserData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.MoMi.db.UserEntity

class HistoriViewModel(
    private val db: TransactionDao
) : ViewModel() {

    val currentUserId: LiveData<Long?> = settingStoreManager.userIdFlow.asLiveData()

    fun getUserData(userId: Long?): LiveData<List<TransactionEntity>> {
        // Implement your logic to retrieve user data based on the provided user ID
        // You can use Room, Retrofit, or any other data source
        // Return the LiveData containing the user data

        if (userId == null) {
            return MutableLiveData<List<TransactionEntity>>().apply {
                value = emptyList()
            }
        } else {
            return db.getAllTransaction(userId)
        }
    }

    fun hapusData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            db.clearData()
        }
    }
}