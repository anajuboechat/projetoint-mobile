package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.Subtopico

class SubtopicoAdapter(
    private val subtopicos: List<Subtopico>,
    private val onItemClick: (Subtopico) -> Unit
) : RecyclerView.Adapter<SubtopicoAdapter.SubtopicoViewHolder>() {

    class SubtopicoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeSubtopico: TextView = itemView.findViewById(R.id.nomeSubtopico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtopicoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subtopico, parent, false)
        return SubtopicoViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubtopicoViewHolder, position: Int) {
        val subtopico = subtopicos[position]
        holder.nomeSubtopico.text = subtopico.nome
        holder.itemView.setOnClickListener { onItemClick(subtopico) }
    }

    override fun getItemCount(): Int = subtopicos.size
}
