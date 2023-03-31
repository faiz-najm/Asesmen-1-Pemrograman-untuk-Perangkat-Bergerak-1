package org.d3if3155.MoMi

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.d3if3155.MoMi.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // Jumlah uang yang dimiliki pengguna
    private var jumlahUang: Int? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menammpilkan nama pengguna dan jumlah uang
        val namaPengguna = arguments?.getString("nama_pengguna")
        jumlahUang = arguments?.getInt("jumlah_uang")

        // intent untuk get data dari FirstFragment
//        val data = requireActivity().intent.getStringExtra("nama_pengguna")
//        val data2 = requireActivity().intent.getIntExtra("jumlah_uang", 0)

        binding.namaPengguna.text = "Halo $namaPengguna"
        binding.uangPengguna.text = "Rp. $jumlahUang"

        binding.buttonSecond.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nama_pengguna", namaPengguna)
            bundle.putInt("jumlah_uang", jumlahUang!!)

//            val fragment = MainFragment()
//            fragment.arguments = bundle

            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment, bundle)
        }

        // Proses menghitung jumlah uang yang di tambahkan atau di kurangi
        binding.buttonProses.setOnClickListener {
            val uangProses = binding.uangProsesInp.text.toString().toInt()

            // Radio button yang dipilih akan menentukan apakah jumlah uang akan ditambahkan atau dikurangi
            if (binding.tambahRadioButton.isChecked) {
                this.jumlahUang = this.jumlahUang?.plus(uangProses)
            } else if (binding.kurangRadioButton.isChecked) {
                this.jumlahUang = this.jumlahUang?.minus(uangProses)
            }

            binding.uangPengguna.text = "Rp. $jumlahUang"

            // Send data to FirstFragment


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}