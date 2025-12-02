package com.projeto.medvest.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.data.Notification
import com.projeto.medvest.ui.adapter.NotificationAdapter
import com.projeto.medvest.ui.util.SpaceItemDecoration

class NotificacoesFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var txtSemNotificacoes: TextView
    private lateinit var btFecharTodas: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private var todasNotificacoes = mutableListOf<Notification>()
    private var notificacoesFechadas = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notificacoes, container, false)

        recycler = view.findViewById(R.id.recyclerNotificacoes)
        txtSemNotificacoes = view.findViewById(R.id.txtSemNotificacoes)
        btFecharTodas = view.findViewById(R.id.btnApagarTudo)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.addItemDecoration(SpaceItemDecoration(20))

        carregarDados()

        btFecharTodas.setOnClickListener { fecharTodas() }

        return view
    }

    /** ============================
     * 1) CARREGAR DADOS DO FIREBASE
     * ============================ */
    private fun carregarDados() {
        val uid = auth.currentUser!!.uid

        // 1) Notificações fechadas
        db.child("usuarios")
            .child(uid)
            .child("notificacoesFechadas")
            .get()
            .addOnSuccessListener { snap ->
                notificacoesFechadas =
                    snap.children.mapNotNull { it.getValue(Int::class.java) }.toMutableList()

                // 2) Todas as notificações
                db.child("notificacoes")
                    .get()
                    .addOnSuccessListener { notifSnap ->
                        todasNotificacoes.clear()

                        notifSnap.children.forEach { n ->
                            n.getValue(Notification::class.java)?.let { todasNotificacoes.add(it) }
                        }

                        atualizarRecycler()
                    }
            }
    }

    /** ============================
     * 2) ATUALIZA LISTA FILTRADA
     * ============================ */
    private fun atualizarRecycler() {
        val formato = java.text.SimpleDateFormat("dd/MM/yyyy")

        val listaFiltrada = todasNotificacoes
            .filter { !notificacoesFechadas.contains(it.id) }
            .sortedByDescending { notif ->
                try {
                    formato.parse(notif.data)
                } catch (e: Exception) {
                    null
                }
            }

        // ------ 🟦 MOSTRAR OU ESCONDER MENSAGEM DE LISTA VAZIA ------
        if (listaFiltrada.isEmpty()) {
            recycler.visibility = View.GONE
            txtSemNotificacoes.visibility = View.VISIBLE
        } else {
            recycler.visibility = View.VISIBLE
            txtSemNotificacoes.visibility = View.GONE
        }
        // -------------------------------------------------------------

        recycler.adapter = NotificationAdapter(listaFiltrada) { item ->
            fecharIndividual(item)
        }
    }

    /** ============================
     * 3) FECHAR UMA NOTIFICAÇÃO
     * ============================ */
    private fun fecharIndividual(notification: Notification) {
        val uid = auth.currentUser!!.uid

        notificacoesFechadas.add(notification.id)

        db.child("usuarios")
            .child(uid)
            .child("notificacoesFechadas")
            .setValue(notificacoesFechadas)
            .addOnSuccessListener { atualizarRecycler() }
    }

    /** ============================
     * 4) FECHAR TODAS AS NOTIFICAÇÕES
     * ============================ */
    private fun fecharTodas() {
        val uid = auth.currentUser!!.uid

        notificacoesFechadas = todasNotificacoes.map { it.id }.toMutableList()

        db.child("usuarios")
            .child(uid)
            .child("notificacoesFechadas")
            .setValue(notificacoesFechadas)
            .addOnSuccessListener { atualizarRecycler() }
    }
}
