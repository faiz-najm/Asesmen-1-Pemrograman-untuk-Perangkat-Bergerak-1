package org.d3if3155.hitungbmi.ui.histori

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentHistoriBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.db.TransactionEntity

class HistoriFragment : Fragment(), HistoriAdapter.OnItemClickListener{

    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    private val viewModel: HistoriViewModel by lazy {
        val db = TransactionDb.getInstance(requireContext())
        val factory = HistoriViewModelFactory(db.transactionDao, db.userDao)
        ViewModelProvider(this, factory)[HistoriViewModel::class.java]
    }

    private lateinit var binding: FragmentHistoriBinding
    private lateinit var myAdapter: HistoriAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoriBinding.inflate(
            layoutInflater,
            container, false
        )

        setHasOptionsMenu(true)

        lifecycleScope.launch {
            layoutDataStore.userIdFlow.collect { userId ->
                viewModel.getUser(userId).observe(viewLifecycleOwner) { users ->
                    viewModel.currentUser.value = users
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myAdapter = HistoriAdapter()
        myAdapter.setOnItemClickListener(this)


        with(binding.recyclerView) {
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = myAdapter
            setHasFixedSize(true)
        }

        viewModel.userTransaction.observe(viewLifecycleOwner) {
            myAdapter.submitList(it)
            binding.emptyView.visibility = if (it.isEmpty())
                View.VISIBLE else View.GONE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.histori_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_hapus) {
            hapusData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hapusData() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.konfirmasi_hapus)
            .setPositiveButton(getString(R.string.hapus)) { _, _ ->
                viewModel.hapusAllData()
            }
            .setNegativeButton(getString(R.string.batal)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun onItemClick(transaction: TransactionEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.konfirmasi_hapus_item)
            .setPositiveButton(getString(R.string.hapus)) { _, _ ->
                viewModel.hapusTransaction(transaction)
            }
            .setNegativeButton(getString(R.string.batal)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
