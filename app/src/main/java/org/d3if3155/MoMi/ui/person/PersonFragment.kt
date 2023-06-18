package org.d3if3155.MoMi.ui.person

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.d3if3155.MoMi.MainActivity
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentPersonBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.db.UserEntity
import org.d3if3155.MoMi.model.fromMoneyFormat
import org.d3if3155.hitungbmi.ui.histori.PersonViewModel
import org.d3if3155.hitungbmi.ui.histori.PersonViewModelFactory
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PersonFragment : Fragment(), View.OnFocusChangeListener, View.OnKeyListener {

    private val TAG = "PersonFragment"

    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    private val viewModel: PersonViewModel by lazy {
        val db = TransactionDb.getInstance(requireContext())
        val factory = PersonViewModelFactory(db.transactionDao, db.userDao)
        ViewModelProvider(this, factory)[PersonViewModel::class.java]
    }

    private var _binding: FragmentPersonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var id: Long = 0
    private var jumlah: Long = 0L
    private val isFisrtTime: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonBinding.inflate(inflater, container, false)

        binding.nameInp.onFocusChangeListener = this
        binding.jumlahUangInp.onFocusChangeListener = this
        binding.jumlahUangInp.setOnKeyListener(this)

        /*val filter = InputFilter { source, _, _, _, _, _ ->
            // Replace spaces with an empty string
            source?.replace(" ".toRegex(), "") ?: ""
        }

        binding.jumlahUangInp.filters = arrayOf(filter)*/

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSimpan.setOnClickListener {
            simpanUser()
        }
    }

    private fun simpanUser() {
        val uangProses = fromMoneyFormat(binding.jumlahUangInp.text.toString())

        if (validasiNama()) {
            binding.nameInp.requestFocus()
            return
        }

        if (uangProses.isNotEmpty()) {
            jumlah = uangProses.toLong()
            if (validasiUang()) return
        } else {
            jumlah = 0L
        }

        val user = UserEntity(
            nama = binding.nameInp.text.toString()
        )

        viewModel.simpanUser(user)

        viewModel.currentUser.observe(viewLifecycleOwner) {

            viewModel.simpanTransaksi(
                this,
                it.id,
                jumlah,
                true
            )

            // Save data to DataStore currentUseID to use in other activity
            runBlocking {
                layoutDataStore.saveUserId(it.id, requireActivity())
                layoutDataStore.saveStarterFinish(!isFisrtTime, requireActivity())
            }

            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validasiUang(): Boolean {
        val uangProses = fromMoneyFormat(binding.jumlahUangInp.text.toString())
        if (uangProses.isNotEmpty()) {
            if (uangProses.toBigInteger() > (Long.MAX_VALUE - 1000).toBigInteger()) {
                binding.jumlahUangHint.error = "Jumlah uang maksimal Rp${Long.MAX_VALUE - 1000}"
                return true
            } else if (uangProses.toBigInteger() < 100.toBigInteger()) {
                binding.jumlahUangHint.error = "Jumlah uang minimal Rp100"
                return true
            } else if (uangProses.toBigInteger() < 0.toBigInteger()) {
                return true
            }
        }
        return false
    }

    private fun validasiNama(): Boolean {
        if (binding.nameInp.text.toString().isEmpty()) {
            binding.nameHint.error = "Nama tidak boleh kosong"
            return true
        } else if (binding.nameInp.text.toString().length < 3) {
            binding.nameHint.error = "Nama minimal 3 karakter"
            return true
        } else if (binding.nameInp.text.toString().length > 20) {
            binding.nameHint.error = "Nama maksimal 20 karakter"
            return true
        }
        return false
    }

    // Option Menu Setting onClick go to SettingFragment

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view) {
                binding.nameInp -> {
                    if (!hasFocus) {
                        validasiNama()
                    }
                }

                binding.jumlahUangInp -> {
                    if (hasFocus) {
                        binding.jumlahUangHint.error = null
                    } else {
                        validasiUang()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {




        if (view != null) {
            when (view.id) {
                binding.jumlahUangInp.id -> {
                    if (binding.jumlahUangInp.text.toString().length > 18) return false
                    lifecycleScope.launch() {
                        val uangProses = binding.jumlahUangInp
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            binding.jumlahUangHint.error = null
                        }

                        // Currency format saat mengetik
                        if (uangProses.text.toString().isNotEmpty()) {
                            val parsedValue =
                                uangProses.text.toString().replace(",", "").toBigInteger()
                            val formattedValue = DecimalFormat("#,###").format(parsedValue)
                            uangProses.setText(formattedValue)
                            uangProses.setSelection(formattedValue.length)
                        }
                    }
                }

                binding.nameInp.id -> {
                    binding.nameHint.error = null
                }
            }
        }
        return false
    }
}