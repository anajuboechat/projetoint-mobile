package com.projeto.medvest.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projeto.medvest.databinding.FragmentFlashcardBinding

class FlashcardFragment : Fragment() {

    private lateinit var binding: FragmentFlashcardBinding
    private val args: FlashcardFragmentArgs by navArgs()
    private var mostrandoFrente = true   // controla se está na frente ou verso

    companion object {
        private const val TAG = "FlashcardFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlashcardBinding.inflate(inflater, container, false)

        /** Aumenta o realismo do efeito 3D */
        val scale = requireContext().resources.displayMetrics.density
        binding.cardContainer.cameraDistance = 12000 * scale

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flashcard = args.flashcard
        Log.d(TAG, "Recebido: frente=${flashcard.frente}, verso=${flashcard.verso}")

        // Definindo textos
        binding.textFrente.text = flashcard.frente
        binding.textTras.text = flashcard.verso

        // Estado inicial
        binding.textFrente.visibility = View.VISIBLE
        binding.textTras.visibility = View.GONE

        /** Clique → FLIP */
        binding.cardContainer.setOnClickListener {
            animarFlip()
        }

        /** Botão para sair */
        binding.btFechar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun animarFlip() {

        val viewOut = if (mostrandoFrente) binding.textFrente else binding.textTras
        val viewIn  = if (mostrandoFrente) binding.textTras else binding.textFrente

        /** Primeira parte do giro → some */
        val animOut = ObjectAnimator.ofFloat(binding.cardContainer, "rotationY", 0f, 90f).apply {
            duration = 150
        }

        /** Segunda parte do giro → aparece */
        val animIn = ObjectAnimator.ofFloat(binding.cardContainer, "rotationY", -90f, 0f).apply {
            duration = 150
        }

        animOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {

                // Troca qual lado está visível
                viewOut.visibility = View.GONE
                viewIn.visibility = View.VISIBLE

                // Alterna estado
                mostrandoFrente = !mostrandoFrente

                animIn.start()
            }
        })

        animOut.start()
    }
}
