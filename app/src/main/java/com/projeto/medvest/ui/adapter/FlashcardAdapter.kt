
package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.databinding.ItemFlashcardBinding
import com.projeto.medvest.data.Flashcard
import android.widget.TextView


class FlashcardAdapter(
    private val lista: List<Flashcard>,
    private val onClick: (Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFrente = view.findViewById<TextView>(R.id.textFrente)

        fun bind(flashcard: Flashcard) {
            textFrente.text = flashcard.frente
            itemView.setOnClickListener {
                onClick(flashcard)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size
}
