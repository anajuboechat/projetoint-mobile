package com.projeto.medvest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto.medvest.R
import com.projeto.medvest.data.Notification

class NotificationAdapter(
    private val lista: List<Notification>,
    private val onDelete: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcone: ImageView = itemView.findViewById(R.id.imgIcone)
        val txtMensagem: TextView = itemView.findViewById(R.id.txtMensagem)
        val txtData: TextView = itemView.findViewById(R.id.txtData)
        val btnFechar: ImageButton = itemView.findViewById(R.id.btnFechar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacao, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = lista[position]

        holder.txtMensagem.text = item.mensagem ?: ""
        holder.txtData.text = item.data ?: ""

        // ----- Correção principal -----
        // Firebase envia "icon", mas pode vir como Long, Int ou String.
        val rawIcon = item.icon ?: 1

        val iconIndex = when (rawIcon) {
            is Long -> rawIcon.toInt()
            is Int -> rawIcon
            is String -> rawIcon.toIntOrNull() ?: 1
            else -> 1
        }
        // -------------------------------

        // Seleção do drawable
        val iconeRes = when (iconIndex) {
            1 -> R.drawable.chat
            2 -> R.drawable.notes
            3 -> R.drawable.dingobell
            else -> R.drawable.chat
        }

        holder.imgIcone.setImageResource(iconeRes)

        holder.btnFechar.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = lista.size
}
