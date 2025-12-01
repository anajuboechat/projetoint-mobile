package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentListaSimuladosBinding
import com.projeto.medvest.util.showBottomSheet

class ListaSimuladosFragment : Fragment() {

    private var _binding: FragmentListaSimuladosBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseDatabase.getInstance().reference.child("Simulados")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaSimuladosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val universidade = arguments?.getString("universidade") ?: "enem"

        val startIndex = when (universidade) {
            "enem" -> 1
            "unicamp" -> 6
            "fuvest" -> 11
            "uerj" -> 16
            else -> 1
        }

        val endIndex = startIndex + 4


        for (i in startIndex..endIndex) {

            val card = layoutInflater.inflate(
                R.layout.item_simulado_card,
                binding.containerSimulados,
                false
            )

            val text = card.findViewById<TextView>(R.id.cardSimuladoText)
            text.text = "${i - startIndex + 1}° Simulado ${universidade.uppercase()}"

            card.setOnClickListener {
                validarSimulado("S$i")
            }

            binding.containerSimulados.addView(card)
        }
    }

    private fun validarSimulado(codigo: String) {
        db.child(codigo).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {

                showBottomSheet(
                    title = "Iniciar Simulado",
                    message = "Você tem certeza que quer iniciar o simulado?",
                    confirmText = "Sim",
                    cancelText = "Não",
                    onConfirm = {
                        val action = ListaSimuladosFragmentDirections
                            .actionListaSimuladosFragmentToTelaSimuladoFragment(codigo)

                        findNavController().navigate(action)
                    }
                )

            } else {

                showBottomSheet(
                    title = "Bloqueado",
                    message = "Esse simulado ainda não foi liberado.",
                    confirmText = null,
                    cancelText = "OK"
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
