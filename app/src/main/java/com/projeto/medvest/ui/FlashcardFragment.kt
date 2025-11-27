package com.projeto.medvest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projeto.medvest.databinding.FragmentFlashcardBinding

class FlashcardFragment : Fragment() {

    private lateinit var binding: FragmentFlashcardBinding
    private val args: FlashcardFragmentArgs by navArgs()

    private var mostrandoFrente = true

    companion object {
        private const val TAG = "FlashcardFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flashcard = args.flashcard

        Log.d(TAG, "Flashcard recebido: id='${flashcard.id}', frente='${flashcard.frente}', verso='${flashcard.verso}'")

        // Conteúdos
        val frenteText = flashcard.frente.ifBlank { "— (sem conteúdo na frente)" }
        val versoText = flashcard.verso.ifBlank { "" }

        // Coloca no layout
        binding.textFrente.text = frenteText
        binding.textTras.text = versoText

        // Começa mostrando a frente
        mostrarFrente()

        // Clique para virar
        binding.cardContainer.setOnClickListener {
            if (mostrandoFrente) {
                if (versoText.isBlank()) {
                    Toast.makeText(requireContext(), "Não há conteúdo na parte de trás.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    mostrarTras()
                }
            } else {
                mostrarFrente()
            }

            mostrandoFrente = !mostrandoFrente
        }

        binding.btFechar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun mostrarFrente() {
        binding.textFrente.visibility = View.VISIBLE
        binding.textTras.visibility = View.GONE
    }

    private fun mostrarTras() {
        binding.textFrente.visibility = View.GONE
        binding.textTras.visibility = View.VISIBLE
    }
}
