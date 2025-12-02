package com.projeto.medvest.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentTelaSimuladoBinding
import com.projeto.medvest.ui.model.SimuladoQuestao

class TelaSimuladoFragment : Fragment() {

    private lateinit var binding: FragmentTelaSimuladoBinding
    private val database = FirebaseDatabase.getInstance().reference

    private var listaQuestoes = mutableListOf<SimuladoQuestao>()
    private var indexAtual = 0
    private var totalQuestoes = 0
    private var codigoSimulado = "S1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTelaSimuladoBinding.inflate(inflater, container, false)

        arguments?.getString("codigoSimulado")?.let {
            codigoSimulado = it
        }

        carregarQuestoes()

        return binding.root
    }

    private fun carregarQuestoes() {
        database.child("Simulados").child(codigoSimulado)
            .get().addOnSuccessListener { snap ->

                listaQuestoes.clear()

                snap.children.forEach { q ->
                    val enunciado = q.child("enunciado").value.toString()
                    val correta = q.child("correta").value.toString()
                    val numero = q.child("numero").value.toString().toInt()

                    val alternativas = mutableListOf<String>()
                    q.child("alternativas").children.forEach {
                        alternativas.add(it.value.toString())
                    }

                    listaQuestoes.add(
                        SimuladoQuestao(
                            numero = numero,
                            enunciado = enunciado,
                            alternativas = alternativas,
                            correta = correta
                        )
                    )
                }

                listaQuestoes.shuffle()
                totalQuestoes = listaQuestoes.size

                montarGrid()
                mostrarQuestao()
            }
    }

    private fun montarGrid() {
        binding.gridQuestoes.removeAllViews()

        listaQuestoes.forEachIndexed { index, _ ->

            val btn = TextView(requireContext())
            btn.text = (index + 1).toString()
            btn.textSize = 18f
            btn.gravity = Gravity.CENTER
            btn.setPadding(10, 10, 10, 10)
            btn.setBackgroundColor(Color.GRAY)
            btn.setTextColor(Color.WHITE)

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            btn.layoutParams = params

            btn.setOnClickListener {
                indexAtual = index
                mostrarQuestao()
            }

            binding.gridQuestoes.addView(btn)
        }
    }

    private fun atualizarGrid() {
        for (i in 0 until binding.gridQuestoes.childCount) {
            val view = binding.gridQuestoes.getChildAt(i) as TextView

            val q = listaQuestoes[i]

            view.setBackgroundResource(
                when {
                    i == indexAtual -> R.drawable.bg_questao_atual
                    q.respostaUsuario != null -> R.drawable.bg_questao_respondida
                    else -> R.drawable.bg_questao_neutra
                }
            )
        }
    }

    private fun mostrarQuestao() {
        val q = listaQuestoes[indexAtual]

        binding.txtEnunciado.text = "${indexAtual + 1}. ${q.enunciado}"

        binding.groupAlternativas.removeAllViews()

        q.alternativas.forEachIndexed { idx, alt ->

            val radio = RadioButton(requireContext())
            radio.text = alt
            radio.textSize = 16f

            radio.setBackgroundResource(R.drawable.bg_alternativa)

            radio.buttonTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.verde_radio)
            )

            val params = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = 12
            params.bottomMargin = 12
            radio.layoutParams = params

            if (q.respostaUsuario == alt) radio.isChecked = true

            radio.setOnClickListener {
                q.respostaUsuario = alt
                atualizarGrid()
            }

            binding.groupAlternativas.addView(radio)
        }

        binding.btnAnterior.isEnabled = indexAtual > 0

        binding.btnProxima.text =
            if (indexAtual == totalQuestoes - 1) "Enviar Respostas"
            else "Próxima"

        binding.btnAnterior.setOnClickListener {
            indexAtual--
            mostrarQuestao()
        }

        binding.btnProxima.setOnClickListener {
            if (indexAtual == totalQuestoes - 1) {
                enviarSimulado()
            } else {
                indexAtual++
                mostrarQuestao()
            }
        }

        atualizarGrid()
    }

    private fun enviarSimulado() {

        val overlay = requireActivity().findViewById<FrameLayout>(R.id.globalOverlay)
        val txt = requireActivity().findViewById<TextView>(R.id.globalOverlayText)

        overlay.visibility = View.VISIBLE
        txt.text = "Enviando respostas..."

        Handler(Looper.getMainLooper()).postDelayed({

            var acertos = 0
            listaQuestoes.forEach {
                val indexResposta = it.alternativas.indexOf(it.respostaUsuario)
                val letraMarcada = when (indexResposta) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    3 -> "D"
                    4 -> "E"
                    else -> ""
                }

                if (letraMarcada == it.correta) {
                    acertos++
                }
            }

            txt.text = "Você fez $acertos acertos!"

            Handler(Looper.getMainLooper()).postDelayed({

                try {
                    val navController = parentFragmentManager
                        .findFragmentById(R.id.nav_host_fragment)
                        ?.findNavController()

                    overlay.visibility = View.GONE
                    navController?.navigate(R.id.menuFragment)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, 3000)

        }, 1500)
    }
}