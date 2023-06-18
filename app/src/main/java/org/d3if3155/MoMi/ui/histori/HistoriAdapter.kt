package org.d3if3155.hitungbmi.ui.histori

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.databinding.ItemHistoriBinding
import org.d3if3155.MoMi.db.TransactionEntity
import org.d3if3155.helloworld.network.CategoryPicApi
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class HistoriAdapter :
    ListAdapter<TransactionEntity, HistoriAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(transaction: TransactionEntity)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<TransactionEntity>() {
                override fun areItemsTheSame(
                    oldData: TransactionEntity, newData: TransactionEntity
                ): Boolean {
                    return oldData.id == newData.id
                }

                override fun areContentsTheSame(
                    oldData: TransactionEntity, newData: TransactionEntity
                ): Boolean {
                    return oldData == newData
                }
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoriBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemHistoriBinding,
        itemClickListener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        )

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    itemClickListener?.onItemClick(item)
                }
            }
        }

        // bind untuk kategori ( menambahan/mengurangan ) dan tanggal transaksi dan jumlah uang
        fun bind(item: TransactionEntity) = with(binding) {
            val colorRes = if (item.type) {
                R.color.tambah
            } else {
                R.color.kurang
            }

            Glide.with(imageView.context)
                .load(CategoryPicApi.getCategoryPicUrl(item.imageId))
                .error(R.drawable.baseline_broken_image_24)
                .into(imageView)


            tanggalTextView.text = dateFormatter.format(item.date)

            val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            numberFormat.minimumFractionDigits = 0


            val formattedNumber = numberFormat.format(item.amount.toString().toLong())

            jumlahTextView.text = formattedNumber
        }
    }
}
