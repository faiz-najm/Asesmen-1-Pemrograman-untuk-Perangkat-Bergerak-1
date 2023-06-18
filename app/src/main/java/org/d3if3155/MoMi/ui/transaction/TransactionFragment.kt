package org.d3if3155.MoMi.ui.transaction

import org.d3if3155.MoMi.model.toMoneyFormat

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentTransactionBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.hitungbmi.ui.histori.TransactionViewModelFactory
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TransactionFragment : Fragment(), View.OnFocusChangeListener, View.OnKeyListener {

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

    var categoryId = 0L
    lateinit var categoryNama: String
    var imageId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        val bundle = arguments

        if (bundle != null) {
            categoryId = bundle.getLong("categoryPicId")
            categoryNama = bundle.getString("categoryPicNama").toString()
            imageId = bundle.getString("imageId").toString()

            binding.categoryInp.setText(categoryNama)
        }

        binding.uangProsesInp.setOnFocusChangeListener(this)
        binding.uangProsesInp.setOnKeyListener(this)

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
            if (it == null) binding.uangPengguna.text = "Rp. 0"
            else binding.uangPengguna.text = toMoneyFormat(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Proses menghitung jumlah uang yang di tambahkan atau di kurangi
        binding.buttonProses.setOnClickListener {
            prosesUang()
        }

        binding.buttonShare.setOnClickListener {
            shareUang()
        }

        binding.categoryInp.setOnClickListener {
            Bundle().apply {
                putLong("categoryId", categoryId)
                putString("categoryPicId", imageId)

                findNavController().navigate(
                    R.id.action_SecondFragment_to_categoryPicFragment,
                    this
                )
            }
        }

        binding.categoryHint.setOnClickListener {
            Bundle().apply {
                putLong("categoryId", categoryId)
                putString("categoryPicId", imageId)

                findNavController().navigate(
                    R.id.action_SecondFragment_to_categoryPicFragment,
                    this
                )
            }
        }

        viewModel.scheduleUpdater(requireActivity().application)
    }


    private fun shareUang() {
        val uangPengguna = binding.uangPengguna.text.toString()
        val namaPengguna = binding.namaPengguna.text.toString()
        val text = "$namaPengguna memiliki uang sebanyak $uangPengguna di applikasi MoMi."
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun prosesUang() {
        if (validasiUang()) return

        val uangProses = binding.uangProsesInp.text.toString().replace(",", "").toLong()

        // Radio button yang dipilih akan menentukan apakah jumlah uang akan ditambahkan atau dikurangi
        if (binding.tambahRadioButton.isChecked) {
            viewModel.currentUser.observe(viewLifecycleOwner) {
                viewModel.addOrSubtractAmount(
                    it.id, uangProses, true, imageId
                )
            }
        } else if (binding.kurangRadioButton.isChecked) {
            viewModel.currentUser.observe(viewLifecycleOwner) {
                viewModel.addOrSubtractAmount(
                    it.id, uangProses, false, imageId
                )
            }
        }
        // setiap data currency amount di view model berubah update text di view
        viewModel.currentUserAmount.observe(viewLifecycleOwner) {
            if (it == null) binding.uangPengguna.text = "Rp. 0"
            else binding.uangPengguna.text = toMoneyFormat(it)
        }
    }

    private fun validasiUang(): Boolean {
        val uangProses = binding.uangProsesInp.text.toString().replace(",", "")

        if (!binding.tambahRadioButton.isChecked && !binding.kurangRadioButton.isChecked) {
            Toast.makeText(
                requireContext(), "Pilih salah satu opsi tambah atau kurang", Toast.LENGTH_SHORT
            ).show()
            return true

        } else if (uangProses.isEmpty()) {
            binding.jumlahUangHint.error = "Jumlah uang tidak boleh kosong"
            return true
        } else if (uangProses.isNotEmpty() && uangProses.toBigInteger() < 100.toBigInteger()) {
            binding.jumlahUangHint.error = "Jumlah uang minimal Rp100"
            return true
        }

        return false
    }


    @Deprecated(
        "Deprecated in Java",
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

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            if (!hasFocus) return
            else if (hasFocus) {
                when (view.id) {
                    binding.uangProsesInp.id -> {
                        binding.uangProsesInp.error = null
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (view != null) {
            when (view.id) {
                binding.uangProsesInp.id -> {
                    val uangProses = binding.uangProsesInp

                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        binding.jumlahUangHint.error = null
                    }

                    // Currency format saat mengetik
                    if (uangProses.text.toString().isNotEmpty()) {
                        val parsedValue = uangProses.text.toString().replace(",", "").toBigInteger()
                        val formattedValue = DecimalFormat("#,###").format(parsedValue)
                        uangProses.setText(formattedValue)
                        uangProses.setSelection(formattedValue.length)
                    }
                }
            }
        }
        return false
    }
}