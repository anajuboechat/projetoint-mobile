package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.projeto.medvest.R

class DetalhesConteudoFragment : Fragment() {

    private var materia: String? = null
    private var subtopico: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var textoView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            materia = it.getString("materia")
            subtopico = it.getString("subtopico")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalhes_conteudo, container, false)

        textoView = view.findViewById(R.id.textoConteudo)
        database = FirebaseDatabase.getInstance().getReference("conteudo")

        carregarTexto()

        return view
    }

    private fun carregarTexto() {
        if (materia == null || subtopico == null) return

        database.child(materia!!).child(subtopico!!).child("texto")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val textoHtml = snapshot.getValue(String::class.java)
                        ?: "Conteúdo não encontrado"

                    // 👉 INTERPRETAR HTML CORRETAMENTE
                    textoView.text = HtmlCompat.fromHtml(
                        textoHtml,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Erro: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
