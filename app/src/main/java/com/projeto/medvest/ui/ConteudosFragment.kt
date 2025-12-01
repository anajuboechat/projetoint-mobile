package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.projeto.medvest.R
import com.projeto.medvest.data.Subtopico
import com.projeto.medvest.ui.adapter.SubtopicoAdapter

class ConteudosFragment : Fragment() {

    private var materia: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var tituloMateria: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materia = arguments?.getString("materia")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conteudos, container, false)
        recyclerView = view.findViewById(R.id.recyclerSubtopicos)
        tituloMateria = view.findViewById(R.id.tituloMateria)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        database = FirebaseDatabase.getInstance().getReference("conteudo")

        tituloMateria.text = materia?.replaceFirstChar { it.uppercase() }

        materia?.let { carregarSubtopicos(it) }

        return view
    }

    private fun carregarSubtopicos(materia: String) {
        database.child(materia).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subtopicos = mutableListOf<Subtopico>()
                for (sub in snapshot.children) {
                    val nome = sub.key ?: ""
                    subtopicos.add(Subtopico(nome))
                }
                recyclerView.adapter = SubtopicoAdapter(subtopicos) { subtopico ->

                    // Navegação usando Navigation Component
                    val bundle = Bundle().apply {
                        putString("materia", materia)
                        putString("subtopico", subtopico.nome)
                    }

                    findNavController().navigate(
                        R.id.action_conteudos_to_detalhesConteudo,
                        bundle
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Erro: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
