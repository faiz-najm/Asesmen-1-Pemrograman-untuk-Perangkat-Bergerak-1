package org.d3if3155.MoMi.ui.categorypic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.d3if3155.MoMi.MainActivity
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.FragmentCategoryPicBinding
import org.d3if3155.MoMi.model.CategoryPic
import org.d3if3155.helloworld.network.ApiStatus
import org.d3if3155.hitungbmi.ui.histori.CategoryPicAdapter
import org.d3if3155.hitungbmi.ui.histori.CategoryPicViewModel
import org.d3if3155.hitungbmi.ui.histori.CategoryPicViewModelFactory


class CategoryPicFragment : Fragment(), CategoryPicAdapter.OnItemClickListener {

    private val layoutDataStore by lazy { SettingDataStore(requireActivity().dataStore) }

    private val viewModel: CategoryPicViewModel by lazy {
        val factory = CategoryPicViewModelFactory()
        ViewModelProvider(this, factory)[CategoryPicViewModel::class.java]
    }

    private lateinit var binding: FragmentCategoryPicBinding
    private lateinit var myAdapter: CategoryPicAdapter

    var categoryPicId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryPicBinding.inflate(
            layoutInflater,
            container, false
        )

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myAdapter = CategoryPicAdapter()

        myAdapter.setOnItemClickListener(this)
        myAdapter.fragment = this

        viewModel.getData().observe(viewLifecycleOwner) {
            myAdapter.updateData(it)
        }

        viewModel.getStatus().observe(viewLifecycleOwner)
        {
            updateProgress(it)
        }

        viewModel.scheduleUpdater(requireActivity().application)

        with(binding.recyclerView) {
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = myAdapter
            setHasFixedSize(true)

        }
    }

    private fun updateProgress(status: ApiStatus) {
        when (status) {
            ApiStatus.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            ApiStatus.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermission()
                }
            }

            ApiStatus.FAILED -> {
                binding.progressBar.visibility = View.GONE
                binding.networkError.visibility = View.VISIBLE
            }
        }
    }

    override fun onItemClick(transaction: CategoryPic) {
        // navigate to trasaction fragment with imageId as argument using safe args
        val bundle = Bundle().apply {
            putLong("categoryPicId", transaction.id)
            putString("categoryPicNama", transaction.nama)
            putString("imageId", transaction.imageId)
        }

        findNavController().previousBackStackEntry?.savedStateHandle?.set("bundleKey", bundle)
        findNavController().popBackStack()

        /*val action = CategoryPicFragmentDirections.actionCategoryPicFragmentToSecondFragment(
            categoryPicId = transaction.id,
            categoryPicNama = transaction.nama,
            imageId = transaction.imageId
        )

        findNavController().navigate(action)*/
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
