package org.d3if3155.MoMi.ui.categorypic

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
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

        val bundle = arguments

        if (bundle != null) {
            categoryPicId = bundle.getLong("categoryPicId")
        }

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

        findNavController().navigate(R.id.action_categoryPicFragment_to_SecondFragment, bundle)


    }


}
