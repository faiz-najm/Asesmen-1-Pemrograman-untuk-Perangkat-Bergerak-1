package org.d3if3155.hitungbmi.ui.histori

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
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
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemHistoriBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        )

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
