package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.Materia

class MateriaAdapter(
    private val lista: List<Materia>,
    private val onClick: (Materia) -> Unit,
    private val onDelete: (Materia) -> Unit
) : RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder>() {

    inner class MateriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.textNomeMateria)
        val btFechar: ImageView = itemView.findViewById(R.id.btFechar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_materia, parent, false)
        return MateriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        val item = lista[position]

        // Nome da matéria
        holder.nome.text = item.nome

        // Clique no card
        holder.itemView.setOnClickListener {
            onClick(item)
        }

        // Clique no botão X para deletar
        holder.btFechar.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = lista.size
}
