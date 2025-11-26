
package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.databinding.ItemFlashcardBinding
import com.projeto.medvest.data.Flashcard

class FlashcardAdapter(
    private val lista: List<Flashcard>
) : RecyclerView.Adapter<FlashcardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFlashcardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.binding.textFrente.text = item.frente
        holder.binding.textVerso.text = item.verso
    }

    override fun getItemCount() = lista.size
}
