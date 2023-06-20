package org.d3if3155.hitungbmi.ui.histori

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.databinding.ItemCategoryBinding
import org.d3if3155.MoMi.model.CategoryPic
import org.d3if3155.MoMi.ui.categorypic.CategoryPicFragment
import org.d3if3155.helloworld.network.CategoryPicApi
import java.util.*

class CategoryPicAdapter :
    ListAdapter<CategoryPic, CategoryPicAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val data = mutableListOf<CategoryPic>()
    private var itemClickListener: OnItemClickListener? = null
    var fragment: CategoryPicFragment? = null

    interface OnItemClickListener {
        fun onItemClick(transaction: CategoryPic)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CategoryPic>() {
                override fun areItemsTheSame(
                    oldData: CategoryPic, newData: CategoryPic
                ): Boolean {
                    return oldData.id == newData.id
                }

                override fun areContentsTheSame(
                    oldData: CategoryPic, newData: CategoryPic
                ): Boolean {
                    return oldData == newData
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(
        private val binding: ItemCategoryBinding,
        itemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        var bundle : Bundle
        var categoryPicId = 0L

        init {
            categoryPicId = 0L
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data[position]
                    itemClickListener?.onItemClick(item)
                }
            }
            bundle = fragment?.requireArguments()!!
            categoryPicId = bundle.getLong("categoryPicId")
        }

        // = with artinya sama dengan binding.apply
        fun bind(item: CategoryPic) = with(binding) {

            Glide.with(imageView.context)
                .load(CategoryPicApi.getCategoryPicUrl(item.imageId))
                .error(R.drawable.baseline_broken_image_24)
                .into(imageView)

            jumlahTextView.text = item.nama

            binding.checkBox.isChecked = item.id == categoryPicId
        }
    }

    fun updateData(newData: List<CategoryPic>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}
