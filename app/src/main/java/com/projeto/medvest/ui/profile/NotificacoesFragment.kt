package com.projeto.medvest.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.projeto.medvest.data.Notificacao
import com.projeto.medvest.databinding.FragmentNotificacoesBinding
import com.projeto.medvest.ui.adapter.NotificacaoAdapter

class NotificacoesFragment : Fragment() {

    private var _binding: FragmentNotificacoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NotificacaoAdapter
    private lateinit var db: DatabaseReference

    private val lista = mutableListOf<Notificacao>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificacoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        db = FirebaseDatabase.getInstance()
            .reference
            .child("usuarios")
            .child(uid)
            .child("notificacoes")

        setupRecycler()
        listenFirebase()
        setupBusca()

        binding.btnLimpar.setOnClickListener {
            db.removeValue()
        }
    }

    private fun setupRecycler() {
        adapter = NotificacaoAdapter(lista) { id ->
            db.child(id.toString()).removeValue()
        }

        binding.recyclerNotificacoes.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerNotificacoes.adapter = adapter
    }

    private fun listenFirebase() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()

                for (child in snapshot.children) {
                    val obj = child.getValue(Notificacao::class.java)
                    if (obj != null) lista.add(obj)
                }

                adapter.updateList(lista)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupBusca() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(text: String?): Boolean {
                val filtered = lista.filter {
                    it.mensagem.contains(text ?: "", ignoreCase = true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
