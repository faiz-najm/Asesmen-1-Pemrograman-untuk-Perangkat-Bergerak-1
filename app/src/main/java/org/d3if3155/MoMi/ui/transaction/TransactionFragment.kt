package org.d3if3155.MoMi.ui.transaction

import android.Manifest
import org.d3if3155.MoMi.model.toMoneyFormat

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.MainActivity
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentTransactionBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.model.fromMoneyFormat
import org.d3if3155.hitungbmi.ui.histori.TransactionViewModelFactory
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TransactionFragment : Fragment(), View.OnFocusChangeListener, View.OnClickListener {

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
    var categoryNama: String = ""
    var imageId: String = ""

    var isRadioButtonChecked = false
    var isJumlahUangValid = false
    var isKeteranganValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

//        val bundle = arguments
        val bundle =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Bundle>("bundleKey")

        if (bundle != null) {
            if (!bundle.isEmpty) {
                categoryId = bundle.getLong("categoryPicId")
                categoryNama = bundle.getString("categoryPicNama").toString()
                imageId = bundle.getString("imageId").toString()

                binding.categoryInp.setText(categoryNama)
            }
        }

        binding.tambahRadioButton.setOnClickListener(this)
        binding.kurangRadioButton.setOnClickListener(this)
        binding.uangProsesInp.onFocusChangeListener = this
//        binding.uangProsesInp.setOnKeyListener(this)

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


        // Set the TextWatcher on the EditText
        binding.uangProsesInp.addTextChangedListener(uangProsesTextWatcher)

        // Proses menghitung jumlah uang yang di tambahkan atau di kurangi
        binding.buttonProses.setOnClickListener {
            prosesUang()
        }

        binding.buttonShare.setOnClickListener {
            shareUang()
        }

        binding.categoryInp.setOnClickListener {
            val action = TransactionFragmentDirections.actionSecondFragmentToCategoryPicFragment(
                categoryPicId = categoryId
            )

            findNavController().navigate(action)
        }

        binding.categoryHint.setOnClickListener {
            val action = TransactionFragmentDirections.actionSecondFragmentToCategoryPicFragment(
                categoryPicId = categoryId
            )

            findNavController().navigate(action)
        }


    }

    override fun onResume() {
        super.onResume()
        binding.categoryInp.setText(categoryNama)

        if (binding.categoryInp.text.toString().isNotEmpty()) {
            isKeteranganValid = true
            enableButtonProses()
        }
    }

    private fun enableButtonProses() {
        binding.buttonProses.isEnabled =
            isRadioButtonChecked && isJumlahUangValid && isKeteranganValid
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
        val uangProses = fromMoneyFormat(binding.uangProsesInp.text.toString()).toLong()

        if (validasiUang()) return

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

        Toast.makeText(requireContext(), "Uang berhasil di proses", Toast.LENGTH_SHORT).show()
    }

    private fun validasiUang(): Boolean {
        val uangProses = fromMoneyFormat(binding.uangProsesInp.text.toString())

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
                view?.findNavController()?.navigate(R.id.action_SecondFragment_to_aboutFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val uangProsesTextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called before the text is changed
            // You can perform any necessary operations here
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This method is called when the text is being changed
            // You can handle the text changes and perform any necessary validations here
            binding.jumlahUangHint.error = null

            if (s?.isNotEmpty() == true) {
                isJumlahUangValid = true
                enableButtonProses()
            } else {
                isJumlahUangValid = false
                enableButtonProses()
            }

            // Currency format saat mengetik
            if (s?.isNotEmpty() == true) {
                val parsedValue = s.toString().replace(",", "").toBigInteger()
                val formattedValue = DecimalFormat("#,###").format(parsedValue)
                binding.uangProsesInp.removeTextChangedListener(this)
                binding.uangProsesInp.setText(formattedValue)
                binding.uangProsesInp.setSelection(formattedValue.length)
                binding.uangProsesInp.addTextChangedListener(this)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // This method is called after the text has changed
            // You can perform any necessary operations here
        }
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

//    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
//        if (view != null) {
//            when (view.id) {
//                binding.uangProsesInp.id -> {
//                    val uangProses = binding.uangProsesInp
//
//                    binding.jumlahUangHint.error = null
//
//                    if (keyCode != KeyEvent.KEYCODE_DEL && binding.uangProsesInp.text.toString()
//                            .isNotEmpty()
//                    ) {
//                        isJumlahUangValid = true
//                        enableButtonProses()
//                    } else if (keyCode == KeyEvent.KEYCODE_DEL && binding.uangProsesInp.text.toString().length == 1) {
//                        isJumlahUangValid = false
//                        enableButtonProses()
//                    }
//
//                    // Currency format saat mengetik
//                    if (uangProses.text.toString().isNotEmpty()) {
//                        val parsedValue = uangProses.text.toString().replace(",", "").toBigInteger()
//                        val formattedValue = DecimalFormat("#,###").format(parsedValue)
//                        uangProses.setText(formattedValue)
//                        uangProses.setSelection(formattedValue.length)
//                    }
//                }
//            }
//        }
//        return false
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.tambahRadioButton.id -> {
                isRadioButtonChecked = true
                enableButtonProses()
            }

            binding.kurangRadioButton.id -> {
                isRadioButtonChecked = true
                enableButtonProses()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                MainActivity.PERMISSION_REQUEST_CODE
            )
        }
    }
}