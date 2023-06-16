package org.d3if3155.hitungbmi.ui.histori

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.d3if3155.MoMi.db.TransactionDao
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.MoMi.db.UserDao
import org.d3if3155.MoMi.db.UserEntity
import org.d3if3155.MoMi.ui.person.PersonFragment

class PersonViewModel(
    private val transactionDb: TransactionDao,
    private val userDb: UserDao
) : ViewModel() {

    private val TAG = PersonFragment::class.java.simpleName

    var currentUser = userDb.getLatestUser()

    fun simpanUser(user: UserEntity) = runBlocking(Dispatchers.IO) {
        userDb.insert(user)
    }

    fun simpanTransaksi(fragment: Fragment, user: Long, jumlah: Int, b: Boolean) {
        runBlocking(Dispatchers.IO) {
            val transaksi = TransactionEntity(
                userId = user,
                amount = jumlah,
                type = b
            )

            transactionDb.insert(transaksi)
        }
    }
}