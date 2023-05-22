package org.d3if3155.hitungbmi.ui.histori

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.d3if3155.MoMi.db.TransactionDao

class HistoriViewModelFactory(
    private val db: TransactionDao

) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoriViewModel::class.java)) {
            return HistoriViewModel(db, ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
