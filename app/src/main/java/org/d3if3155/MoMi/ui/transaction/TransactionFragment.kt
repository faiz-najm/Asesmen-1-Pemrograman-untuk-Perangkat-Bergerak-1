package org.d3if3155.MoMi.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentTransactionBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.hitungbmi.ui.histori.TransactionViewModelFactory

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TransactionFragment : Fragment() {

    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    // viewmodel
    private val viewModel: TransactionViewModel by lazy {
        val db = TransactionDb.getInstance(requireContext())
        val factory = TransactionViewModelFactory(db.transactionDao, db.userDao)
        ViewModelProvider(this, factory)[TransactionViewModel::class.java]
    }

    private var _binding: FragmentTransactionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        lifecycleScope.launch {
            layoutDataStore.userIdFlow.collect { userId ->
                viewModel.getUser(userId).observe(viewLifecycleOwner) { users ->
                    viewModel.currentUser.value = users
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {
            binding.namaPengguna.text = "Halo ${it.nama}"
        }

        viewModel.currentUserAmount.observe(viewLifecycleOwner) {
            if (it == null)
                binding.uangPengguna.text = "Rp. 0"
            else
                binding.uangPengguna.text = "Rp. $it"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menampilkan nama pengguna dan jumlah uang
        lifecycleScope.launch {
            // Proses menghitung jumlah uang yang di tambahkan atau di kurangi
            binding.buttonProses.setOnClickListener {
                val uangProses = binding.uangProsesInp.text.toString().toInt()

                // Radio button yang dipilih akan menentukan apakah jumlah uang akan ditambahkan atau dikurangi
                if (binding.tambahRadioButton.isChecked) {
                    viewModel.currentUser.observe(viewLifecycleOwner) {
                        viewModel.addOrSubtractAmount(
                            it.id, uangProses, true
                        )
                    }
                } else if (binding.kurangRadioButton.isChecked) {
                    viewModel.currentUser.observe(viewLifecycleOwner) {
                        viewModel.addOrSubtractAmount(
                            it.id, uangProses, false
                        )
                    }
                }
                // setiap data currency amount di view model berubah update text di view
                viewModel.currentUserAmount.observe(viewLifecycleOwner) {
                    binding.uangPengguna.text = "Rp. $it"
                }
            }
        }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("inflater.inflate(R.menu.menu_main, menu)", "org.d3if3155.MoMi.R")
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_histori -> {
                view?.findNavController()?.navigate(R.id.action_SecondFragment_to_historiFragment)
            }

            R.id.action_tentang -> {
//                view?.findNavController()?.navigate(R.id.action_TransactionFragment_to_SettingFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}