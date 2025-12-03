package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.projeto.medvest.R

class DetalhesConteudoFragment : Fragment() {

    private var materia: String? = null
    private var subtopico: String? = null
    private lateinit var textoView: TextView
    private lateinit var btnIrQuestoes: Button
    private lateinit var databaseConteudo: DatabaseReference
    private lateinit var databaseQuestoes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            materia = it.getString("materia")
            subtopico = it.getString("subtopico")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalhes_conteudo, container, false)

        textoView = view.findViewById(R.id.textoConteudo)
        btnIrQuestoes = view.findViewById(R.id.btnIrParaQuestoes)

        databaseConteudo = FirebaseDatabase.getInstance().getReference("conteudo")
        databaseQuestoes = FirebaseDatabase.getInstance().getReference("questoes")

        carregarTexto()
        verificarQuestoes()

        return view
    }

    private fun carregarTexto() {
        if (materia.isNullOrEmpty() || subtopico.isNullOrEmpty()) {
            textoView.text = "Conteúdo não disponível"
            return
        }

        databaseConteudo.child(materia!!).child(subtopico!!).child("texto")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val textoHtml = snapshot.getValue(String::class.java)
                    if (textoHtml.isNullOrEmpty()) {
                        textoView.text = "Conteúdo não encontrado"
                    } else {
                        textoView.text = HtmlCompat.fromHtml(
                            textoHtml,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar conteúdo: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    /**
     * Mostra o botão se existirem questões do subtopico.
     */
    private fun verificarQuestoes() {
        if (materia == null || subtopico == null) return

        databaseQuestoes.child(materia!!).child(subtopico!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    // Só aparece se houver pelo menos UMA questão
                    if (snapshot.exists()) {
                        btnIrQuestoes.visibility = View.VISIBLE

                        btnIrQuestoes.setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("materia", materia)
                                putString("subtopico", subtopico)
                            }

                            findNavController().navigate(
                                R.id.action_detalhesConteudo_to_questoesfragment,
                                bundle
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}