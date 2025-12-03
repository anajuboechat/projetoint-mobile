package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.android.material.card.MaterialCardView
import com.projeto.medvest.R

data class EstadoQuestao(
    var respondida: Boolean = false,
    var alternativaSelecionada: String? = null,
    var acertou: Boolean? = null
)

class QuestoesFragment : Fragment() {

    private var materia: String? = null
    private var subtopico: String? = null

    private lateinit var tvTituloConteudo: TextView
    private lateinit var tvInfoQuestoes: TextView
    private lateinit var tvTextoQuestao: TextView
    private lateinit var containerAlternativas: LinearLayout

    private lateinit var btnAnterior: Button
    private lateinit var btnProxima: Button
    private lateinit var btnConfirmar: Button

    private lateinit var dbQuestoes: DatabaseReference

    private val listaQuestoes = mutableListOf<DataSnapshot>()
    private val estadoQuestoes = mutableListOf<EstadoQuestao>()

    private var indiceAtual = 0
    private var alternativaTempSelecionada: String? = null

    private var acertos = 0
    private var erros = 0
    private var jaConfirmado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materia = arguments?.getString("materia")
        subtopico = arguments?.getString("subtopico")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_questoes, container, false)

        tvTituloConteudo = view.findViewById(R.id.tvTituloConteudo)
        tvInfoQuestoes = view.findViewById(R.id.tvInfoQuestoes)
        tvTextoQuestao = view.findViewById(R.id.tvTextoQuestao)
        containerAlternativas = view.findViewById(R.id.containerAlternativas)

        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnProxima = view.findViewById(R.id.btnProxima)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)

        tvTituloConteudo.text = subtopico?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        } ?: ""

        dbQuestoes = FirebaseDatabase.getInstance().getReference("questoes")

        btnAnterior.setOnClickListener {
            if (indiceAtual > 0) {
                indiceAtual--
                mostrarQuestao()
            }
        }

        btnProxima.setOnClickListener {
            if (indiceAtual < listaQuestoes.size - 1) {
                indiceAtual++
                mostrarQuestao()
            }
        }

        btnConfirmar.setOnClickListener {
            if (!jaConfirmado && alternativaTempSelecionada != null) {
                confirmarResposta()
            }
        }

        carregarQuestoes()

        return view
    }

    private fun carregarQuestoes() {
        if (materia == null || subtopico == null) return

        dbQuestoes.child(materia!!).child(subtopico!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaQuestoes.clear()
                    estadoQuestoes.clear()

                    for (q in snapshot.children) {
                        listaQuestoes.add(q)
                    }

                    estadoQuestoes.addAll(List(listaQuestoes.size) { EstadoQuestao() })

                    if (listaQuestoes.isNotEmpty()) {
                        indiceAtual = 0
                        acertos = 0
                        erros = 0
                        mostrarQuestao()
                    } else {
                        tvTextoQuestao.text = "Nenhuma questão encontrada."
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun mostrarQuestao() {
        containerAlternativas.removeAllViews()
        alternativaTempSelecionada = null

        val estado = estadoQuestoes[indiceAtual]
        val q = listaQuestoes[indiceAtual]

        tvTextoQuestao.text = q.child("enunciado").getValue(String::class.java) ?: ""

        atualizarHeader()

        val alternativas = q.child("alternativas")
        val letras = listOf("A", "B", "C", "D", "E")

        letras.forEachIndexed { index, letra ->
            val texto = alternativas.child(index.toString()).getValue(String::class.java) ?: ""
            val optionView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_alternativa, containerAlternativas, false)

            val card = optionView.findViewById<MaterialCardView>(R.id.cardOption)
            val tvLetra = optionView.findViewById<TextView>(R.id.tvLetra)
            val tvTexto = optionView.findViewById<TextView>(R.id.tvTexto)

            tvLetra.text = letra
            tvTexto.text = texto

            card.setOnClickListener {
                if (jaConfirmado) return@setOnClickListener
                alternativaTempSelecionada = letra
                btnConfirmar.isEnabled = true
                marcarSelecionada(card)
            }

            containerAlternativas.addView(optionView)
        }

        if (estado.respondida) {
            jaConfirmado = true
            pintarResultadoAnterior()
            btnConfirmar.isEnabled = false
        } else {
            jaConfirmado = false
            btnConfirmar.isEnabled = false
        }
    }

    private fun marcarSelecionada(cardSelecionado: MaterialCardView) {
        for (i in 0 until containerAlternativas.childCount) {
            val child = containerAlternativas.getChildAt(i)
            val card = child.findViewById<MaterialCardView>(R.id.cardOption)
            card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_normal_bg))
            card.strokeWidth = 0
        }

        cardSelecionado.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_selecionada_bg))
        cardSelecionado.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width_default)
        cardSelecionado.strokeColor = ContextCompat.getColor(requireContext(), R.color.opcao_selecionada_stroke)
    }

    private fun confirmarResposta() {
        val estado = estadoQuestoes[indiceAtual]

        if (estado.respondida) {
            jaConfirmado = true
            return
        }

        val q = listaQuestoes[indiceAtual]
        val correta = q.child("correta").getValue(String::class.java) ?: ""

        val acertou = (alternativaTempSelecionada == correta)

        estado.respondida = true
        estado.acertou = acertou
        estado.alternativaSelecionada = alternativaTempSelecionada

        if (acertou) acertos++ else erros++

        pintarResultado()

        jaConfirmado = true
        btnConfirmar.isEnabled = false
        atualizarHeader()
    }

    private fun pintarResultado() {
        val q = listaQuestoes[indiceAtual]
        val correta = q.child("correta").getValue(String::class.java) ?: ""

        for (i in 0 until containerAlternativas.childCount) {
            val child = containerAlternativas.getChildAt(i)
            val card = child.findViewById<MaterialCardView>(R.id.cardOption)
            val letra = child.findViewById<TextView>(R.id.tvLetra).text.toString()

            when {
                letra == correta -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_correta_bg))
                    card.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width_default)
                    card.strokeColor = ContextCompat.getColor(requireContext(), R.color.opcao_correta_stroke)
                }
                letra == alternativaTempSelecionada -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_errada_bg))
                    card.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width_default)
                    card.strokeColor = ContextCompat.getColor(requireContext(), R.color.opcao_errada_stroke)
                }
                else -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_normal_bg))
                    card.strokeWidth = 0
                }
            }

            card.isClickable = false
        }
    }

    private fun pintarResultadoAnterior() {
        val q = listaQuestoes[indiceAtual]
        val correta = q.child("correta").getValue(String::class.java) ?: ""
        val estado = estadoQuestoes[indiceAtual]

        for (i in 0 until containerAlternativas.childCount) {
            val child = containerAlternativas.getChildAt(i)
            val card = child.findViewById<MaterialCardView>(R.id.cardOption)
            val letra = child.findViewById<TextView>(R.id.tvLetra).text.toString()

            when {
                letra == correta -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_correta_bg))
                    card.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width_default)
                    card.strokeColor = ContextCompat.getColor(requireContext(), R.color.opcao_correta_stroke)
                }
                letra == estado.alternativaSelecionada && estado.acertou == false -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.opcao_errada_bg))
                    card.strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_width_default)
                    card.strokeColor = ContextCompat.getColor(requireContext(), R.color.opcao_errada_stroke)
                }
            }

            card.isClickable = false
        }
    }

    private fun atualizarHeader() {
        tvInfoQuestoes.text = "Questão ${indiceAtual + 1} de ${listaQuestoes.size}   ✓$acertos | ✗$erros"
    }
}