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

class PersonViewModel(
    private val db: TransactionDao,
    private val settingStoreManager: SettingDataStore
) : ViewModel() {


}