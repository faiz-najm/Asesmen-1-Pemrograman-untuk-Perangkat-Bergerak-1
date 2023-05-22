package org.d3if3155.MoMi.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.d3if3155.MoMi.databinding.ItemStarterBinding
import org.d3if3155.MoMi.model.Starter

class OnboardingAdapter(
) : RecyclerView.Adapter<OnboardingAdapter.StarterViewHolder>() {

    private val data = mutableListOf<Starter>()

    class StarterViewHolder(private val binding: ItemStarterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(starter: Starter) = with(binding) {
            imageStarter.setImageResource(starter.image)
            judulStarter.text = starter.judul
            deskStarter.text = starter.desk
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StarterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStarterBinding.inflate(inflater, parent, false)
        return StarterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StarterViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(newData: List<Starter>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}