package org.d3if3155.MoMi.ui.person

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.d3if3155.MoMi.MainActivity
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentPersonBinding
import org.d3if3155.MoMi.db.TransactionDb
import org.d3if3155.MoMi.db.UserEntity
import org.d3if3155.hitungbmi.ui.histori.PersonViewModel
import org.d3if3155.hitungbmi.ui.histori.PersonViewModelFactory


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PersonFragment : Fragment() {

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
    private var namaPengguna: String? = ""
    private var jumlah: Int = 0
    private val isFisrtTime: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPersonBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSimpan.setOnClickListener {
            simpanUser()
        }
    }

    private fun simpanUser() {
        namaPengguna = binding.nameInp.text.toString()
        val jumlahUang = binding.jumlahUangInp.text.toString()
        jumlah = jumlahUang.toInt()

        // validasi
        if (namaPengguna!!.isEmpty()) {
            Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (jumlahUang.isEmpty()) {
            Toast.makeText(context, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
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

    // Option Menu Setting onClick go to SettingFragment

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Simpan nama pengguna ke Bundle
        outState.putString("nama_pengguna", namaPengguna)
    }
}