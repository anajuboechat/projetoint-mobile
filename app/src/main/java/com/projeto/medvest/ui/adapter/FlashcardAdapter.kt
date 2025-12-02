package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.Flashcard

class FlashcardAdapter(
    private var lista: MutableList<Flashcard>,
    private val onItemClick: (Flashcard) -> Unit,
    private val onDeleteClick: (Flashcard) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textFrente: TextView = itemView.findViewById(R.id.textFrente)
        private val btDelete: ImageView = itemView.findViewById(R.id.btFechar)

        fun bind(flashcard: Flashcard) {
            textFrente.text = flashcard.frente

            // Clique no flashcard abre o conteúdo
            itemView.setOnClickListener {
                onItemClick(flashcard)
            }

            // Clique no X exclui
            btDelete.setOnClickListener {
                onDeleteClick(flashcard)
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

    fun atualizarLista(novaLista: List<Flashcard>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }
}
