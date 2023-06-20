package org.d3if3155.MoMi.ui.transaction

import org.d3if3155.MoMi.model.toMoneyFormat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentTransactionBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.model.fromMoneyFormat
import org.d3if3155.hitungbmi.ui.histori.TransactionViewModelFactory
import java.text.DecimalFormat

class TransactionFragment : Fragment(), MenuProvider {

    private val layoutDataStore by lazy {
        SettingDataStore(requireActivity().dataStore)
    }
    private val viewModel by lazy {
        val db = TransactionDb.getInstance(requireContext())
        val userDb = TransactionDb.getInstance(requireContext())
        val factory = TransactionViewModelFactory(db.transactionDao, userDb.userDao)
        ViewModelProvider(this, factory)[TransactionViewModel::class.java]
    }

    private lateinit var binding: FragmentTransactionBinding

    private var isRadioButtonChecked = false
    private var isJumlahUangValid = false
    private var isKeteranganValid = false

    private lateinit var imageId: String
    private var categoryNama: String = ""
    private var categoryId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)

        val bundle =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Bundle>("bundleKey")

        if (bundle != null) {
            categoryId = bundle.getLong("categoryPicId")
            categoryNama = bundle.getString("categoryPicNama").toString()
            imageId = bundle.getString("imageId").toString()

            binding.keteranganInp.setText(categoryNama)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCurrentUser()
        observeCurrentUserAmount()
        setupListeners()

        lifecycleScope.launch {
            layoutDataStore.userIdFlow.collect { userId ->
                viewModel.getUser(userId).observe(viewLifecycleOwner) { users ->
                    viewModel.currentUser.value = users
                }
            }
        }

    }

    private fun observeCurrentUser() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.namaPengguna.text = "Halo ${user.nama}"
        }
    }

    private fun observeCurrentUserAmount() {
        viewModel.currentUserAmount.observe(viewLifecycleOwner) { amount ->
            binding.uangPengguna.text = amount?.let { toMoneyFormat(it) } ?: "Rp0"
        }
    }

    private fun setupListeners() {

        binding.uangProsesInp.addTextChangedListener(textWatcher)

        binding.tambahRadioButton.setOnClickListener {
            isRadioButtonChecked = true
            enableButtonProses()
        }

        binding.kurangRadioButton.setOnClickListener {
            isRadioButtonChecked = true
            enableButtonProses()
        }

        binding.buttonProses.setOnClickListener {
            prosesUang()
        }

        binding.buttonShare.setOnClickListener {
            shareUang()
        }

        binding.keteranganInp.setOnClickListener {
            val action = TransactionFragmentDirections.actionSecondFragmentToCategoryPicFragment(
                categoryPicId = categoryId
            )
            findNavController().navigate(action)
        }
    }

    private fun validateJumlahUang(jumlahUang: String) {
        isJumlahUangValid =
            jumlahUang.isNotEmpty() && jumlahUang.toBigInteger() >= 100.toBigInteger()
        enableButtonProses()
    }

    private fun formatUangProsesInput(jumlahUang: String) {
        val parsedValue = fromMoneyFormat(jumlahUang).toBigInteger()
        val formattedValue = DecimalFormat("#,###").format(parsedValue)
        binding.uangProsesInp.removeTextChangedListener(textWatcher)
        binding.uangProsesInp.setText(formattedValue)
        binding.uangProsesInp.setSelection(formattedValue.length)
        binding.uangProsesInp.addTextChangedListener(textWatcher)
    }

    private fun enableButtonProses() {
        binding.buttonProses.isEnabled =
            isRadioButtonChecked && isJumlahUangValid && isKeteranganValid
    }

    private fun shareUang() {
        val uangPengguna = binding.uangPengguna.text.toString()
        val keterangan = binding.keteranganInp.text.toString()
        val shareText = "Halo, saya baru saja $keterangan sebesar $uangPengguna"
        // Implement share intent with shareText

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.type = "text/plain"
        startActivity(intent)
    }

    private fun prosesUang() {

        val uangProses = fromMoneyFormat(binding.uangProsesInp.text.toString()).toLong()

        val isTambah = binding.tambahRadioButton.isChecked

        viewModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser != null) {
                viewModel.addOrSubtractAmount(
                    currentUser.id, uangProses, isTambah, imageId
                )

                // Observe the updated amount and update the view
                viewModel.currentUserAmount.observe(viewLifecycleOwner) { updatedAmount ->
                    if (updatedAmount == null) {
                        binding.uangPengguna.text = "Rp0"
                        return@observe
                    }
                    binding.uangPengguna.text = toMoneyFormat(updatedAmount)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Terjadi kesalahan saat memproses uang",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        showToast("Uang berhasil diproses")
        clearInputFields()
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // No action needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Update validation and format uangProsesInp
            if (s!!.isEmpty()) return

            val formatted = fromMoneyFormat(s.toString())
            validateJumlahUang(formatted)
            formatUangProsesInput(formatted)
        }

        override fun afterTextChanged(s: Editable?) {
            // No action needed
        }
    }

    private fun clearInputFields() {
        binding.uangProsesInp.text?.clear()
        binding.keteranganInp.text.clear()
        binding.keteranganInp.clearFocus()

        isRadioButtonChecked = false
        isJumlahUangValid = false
        isKeteranganValid = false

        binding.tambahRadioButton.isChecked = false
        binding.kurangRadioButton.isChecked = false

        enableButtonProses()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()

        binding.keteranganInp.setText(categoryNama)

        if (categoryId != 0L) {
            isKeteranganValid = true
            enableButtonProses()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_histori -> {
                findNavController().navigate(R.id.action_SecondFragment_to_historiFragment)
                return false
            }

            R.id.action_tentang -> {
                findNavController().navigate(R.id.action_SecondFragment_to_aboutFragment)
                return false
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}
