package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.Notificacao
import com.projeto.medvest.databinding.ItemNotificacaoBinding

class NotificacaoAdapter(
    private var lista: MutableList<Notificacao>,
    private val onDelete: (Int) -> Unit    // agora passa o id NUMÉRICO
) : RecyclerView.Adapter<NotificacaoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificacaoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(not: Notificacao) {
            binding.textTitulo.text = not.mensagem
            binding.textData.text = not.data

            binding.btnExcluir.setOnClickListener {
                onDelete(not.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificacaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Notificacao>) {
        lista = newList.toMutableList()
        notifyDataSetChanged()
    }
}
