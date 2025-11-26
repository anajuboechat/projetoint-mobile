package com.projeto.medvest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projeto.medvest.data.Flashcard
import com.projeto.medvest.databinding.FragmentFlashcardViewBinding


class FlashcardViewFragment : Fragment() {

    private var mostrandoFrente = true
    private lateinit var flashcard: Flashcard
    private lateinit var binding: FragmentFlashcardViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlashcardViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flashcard = FlashcardViewFragmentArgs.fromBundle(requireArguments()).flashcard

        mostrarFrente()

        binding.textConteudo.setOnClickListener {
            if (mostrandoFrente) {
                mostrarTras()
            } else {
                mostrarFrente()
            }
        }
    }

    private fun mostrarFrente() {
        binding.textConteudo.text = flashcard.frente
        mostrandoFrente = true
    }

    private fun mostrarTras() {
        binding.textConteudo.text = flashcard.tras
        mostrandoFrente = false
    }
}
