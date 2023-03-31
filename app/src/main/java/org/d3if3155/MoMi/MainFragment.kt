package org.d3if3155.MoMi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.d3if3155.MoMi.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var namaPengguna: String? = null
    private var jumlah: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil nama pengguna dari Bundle
        namaPengguna = arguments?.getString("nama_pengguna")

        // Always Enabling button next
        if (namaPengguna != null) {
            binding.buttonFirst.isEnabled = true
        }

        binding.buttonFirst.setOnClickListener {
            val namaPengguna = arguments?.getString("nama_pengguna")
            val jumlahUang = arguments?.getInt("jumlah_uang")

            val bundle = Bundle()
            bundle.putString("nama_pengguna", namaPengguna)
            bundle.putInt("jumlah_uang", jumlahUang!!)

            // Navigate to second fragment
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }

        binding.buttonSimpan.setOnClickListener {
            // nama dan jumlah
            namaPengguna = binding.nameInp.text.toString()
            var jumlahUang = binding.jumlahUangInp.text.toString()
            jumlah = jumlahUang.toInt()

            // validasi
            if (namaPengguna!!.isEmpty()) {
                Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (jumlahUang.isEmpty()) {
                Toast.makeText(context, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Reset inputan
            binding.nameInp.text!!.clear()
            binding.jumlahUangInp.text!!.clear()

            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

            // Gunakan nama dan jumlah untuk mengirim data ke fragment selanjutnya
            /*val bundle1 = Bundle()
            bundle.putString("nama_pengguna", nama)
            bundle.putString("jumlah_uang", jumlah)*/

            val bundle = Bundle().apply {
                putString("nama_pengguna", namaPengguna)
                putInt("jumlah_uang", jumlah)
            }

            binding.buttonFirst.isEnabled = true

            // Navigate to second fragment
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)

        }
    }

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