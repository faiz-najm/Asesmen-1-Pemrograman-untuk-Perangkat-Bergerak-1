package org.d3if3155.hitungbmi.ui.histori

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.UserDao
import org.d3if3155.MoMi.ui.transaction.TransactionViewModel

class TransactionViewModelFactory(
    private val db: TransactionDao,
    private val userDb: UserDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(db, userDb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
