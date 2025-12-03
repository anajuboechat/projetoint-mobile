package com.projeto.medvest.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.databinding.ItemVestibularBinding
import com.projeto.medvest.ui.model.Vestibular

class VestibularAdapter(
    private val lista: List<Vestibular>,
    private val onSelect: (Vestibular) -> Unit
) : RecyclerView.Adapter<VestibularAdapter.VestibularViewHolder>() {

    private val selecionados = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VestibularViewHolder {
        val binding = ItemVestibularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VestibularViewHolder(binding)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VestibularViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    inner class VestibularViewHolder(private val binding: ItemVestibularBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(v: Vestibular) {

            // Imagem
            binding.imgVestibular.setImageResource(v.imagem)

            // Texto
            binding.txtNomeVestibular.text = v.nome

            // Verifica se está selecionado
            val isSelected = selecionados.contains(v.nome)

            // Check
            binding.imgCheck.visibility = if (isSelected) View.VISIBLE else View.GONE

            // Cor do card
            binding.cardContainer.setCardBackgroundColor(
                if (isSelected) Color.parseColor("#D5EED1")
                else Color.WHITE
            )

            // Clique no card
            binding.root.setOnClickListener {
                if (isSelected) selecionados.remove(v.nome)
                else selecionados.add(v.nome)

                notifyItemChanged(adapterPosition)
                onSelect(v)
            }
        }
    }

    fun getSelecionados(): Set<String> = selecionados
}