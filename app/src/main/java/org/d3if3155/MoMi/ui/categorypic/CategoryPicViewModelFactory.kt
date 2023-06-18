package org.d3if3155.hitungbmi.ui.histori

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.UserDao

class CategoryPicViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryPicViewModel::class.java)) {
            return CategoryPicViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
