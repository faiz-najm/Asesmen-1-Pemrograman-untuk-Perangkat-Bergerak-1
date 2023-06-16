package org.d3if3155.hitungbmi.ui.histori

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.databinding.ItemHistoriBinding
import org.d3if3155.MoMi.db.TransactionEntity
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

            val circleBg = typeTextView.background as GradientDrawable
            circleBg.setColor(ContextCompat.getColor(root.context, colorRes))

            typeTextView.text = if (item.type) {
                "+"
            } else {
                "-"
            }

            tanggalTextView.text = dateFormatter.format(item.date)
            jumlahTextView.text = item.amount.toString()
        }
    }
}
