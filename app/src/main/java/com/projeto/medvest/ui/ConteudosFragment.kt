package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
    private lateinit var btnVerQuestoes: Button
    private lateinit var databaseConteudo: DatabaseReference
    private lateinit var databaseQuestoes: DatabaseReference

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
        btnVerQuestoes = view.findViewById(R.id.btnVerQuestoes)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseConteudo = FirebaseDatabase.getInstance().getReference("conteudo")
        databaseQuestoes = FirebaseDatabase.getInstance().getReference("questoes")

        tituloMateria.text = materia?.replaceFirstChar { it.uppercase() }

        materia?.let { carregarSubtopicos(it) }

        return view
    }

    private fun carregarSubtopicos(materia: String) {
        databaseConteudo.child(materia).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subtopicos = mutableListOf<Subtopico>()
                var primeiroSubtopico: String? = null

                for (sub in snapshot.children) {
                    val nome = sub.key ?: ""
                    if (primeiroSubtopico == null) primeiroSubtopico = nome
                    subtopicos.add(Subtopico(nome))
                }

                recyclerView.adapter = SubtopicoAdapter(subtopicos) { subtopico ->

                    val bundle = Bundle().apply {
                        putString("materia", materia)
                        putString("subtopico", subtopico.nome)
                    }

                    findNavController().navigate(
                        R.id.action_conteudos_to_detalhesConteudo,
                        bundle
                    )
                }

                // Após carregar subtopicos, verificar se há questões
                primeiroSubtopico?.let { verificarQuestoes(materia, it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Erro: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verificarQuestoes(materia: String, subtopico: String) {
        databaseQuestoes.child(materia).child(subtopico)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        btnVerQuestoes.visibility = View.VISIBLE

                        btnVerQuestoes.setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("materia", materia)
                                putString("subtopico", subtopico)
                            }

                            findNavController().navigate(
                                R.id.action_conteudos_to_questoesFragment,
                                bundle
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}