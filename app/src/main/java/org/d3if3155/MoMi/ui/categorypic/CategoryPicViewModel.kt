package org.d3if3155.hitungbmi.ui.histori

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.model.CategoryPic
import org.d3if3155.MoMi.network.UpdateWorker
import org.d3if3155.helloworld.network.CategoryPicApi
import org.d3if3155.helloworld.network.ApiStatus
import java.util.concurrent.TimeUnit

class CategoryPicViewModel(
) : ViewModel() {
    private val data = MutableLiveData<List<CategoryPic>>()
    private val status = MutableLiveData<ApiStatus>()

    init {
        retrieveData()
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

    fun getData(): LiveData<List<CategoryPic>> = data

    fun getStatus(): LiveData<ApiStatus> = status

    fun scheduleUpdater(app: Application) {
        val request = OneTimeWorkRequestBuilder<UpdateWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(app).enqueueUniqueWork(
            UpdateWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

}