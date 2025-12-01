package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.DisciplinaComMaterias
import com.projeto.medvest.data.Materia

class DisciplinaAdapter(
    private val lista: List<DisciplinaComMaterias>,
    private val onClick: (Materia) -> Unit,
    private val onDelete: (Materia) -> Unit
) : RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder>() {

    inner class DisciplinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeDisciplina: TextView = itemView.findViewById(R.id.textDisciplina)
        val recyclerMaterias: RecyclerView = itemView.findViewById(R.id.recyclerMaterias)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_disciplina, parent, false)
        return DisciplinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisciplinaViewHolder, position: Int) {
        val item = lista[position]

        holder.nomeDisciplina.text = item.disciplina

        holder.recyclerMaterias.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        holder.recyclerMaterias.adapter =
            MateriaAdapter(item.materias, onClick, onDelete)
    }

    override fun getItemCount() = lista.size
}
