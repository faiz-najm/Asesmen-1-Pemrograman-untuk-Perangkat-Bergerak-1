package org.d3if3155.MoMi.ui.onboarding

import OnboardingViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentOnboardingBinding


class OnboardingFragment : Fragment() {

    // layout data store adalah variabel yang akan digunakan untuk mengakses data store
    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    // lazy adalah fungsi yang akan dijalankan ketika variabel tersebut dipanggil
    // by lazy akan membuat variabel tersebut menjadi singleton
    private val viewModel: OnboardingViewModel by lazy {
        ViewModelProvider(this)[OnboardingViewModel::class.java]
    }

    private var _binding: FragmentOnboardingBinding? = null
    private var myAdapter: OnboardingAdapter? = null
    private val binding get() = _binding!!
    private val isFisrtTime: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root

        // setup the viewpager
        myAdapter = OnboardingAdapter()

        binding.viewPager2.adapter = myAdapter
        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // setup the indicator
        binding.indicatorViewPager2.setViewPager(binding.viewPager2)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Jika buttonGetStarted di klik, maka akan menjalankan kode di dalamnya yaitu menjalankan main activity dan mengakhiri activity ini
        binding.buttonGetStarted.setOnClickListener {
            viewModel.viewModelScope.launch {
                layoutDataStore.saveFirstTime(!isFisrtTime, requireActivity())
            }
            activity?.finish()
        }

        viewModel.getData().observe(viewLifecycleOwner) { data ->
            myAdapter?.updateData(data)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}