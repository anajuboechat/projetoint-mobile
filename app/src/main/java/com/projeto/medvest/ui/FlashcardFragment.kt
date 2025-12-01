package com.projeto.medvest.ui

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.projeto.medvest.data.Flashcard
import com.projeto.medvest.databinding.FragmentFlashcardBinding

class FlashcardFragment : Fragment() {

    private lateinit var binding: FragmentFlashcardBinding
    private val args: FlashcardFragmentArgs by navArgs()

    private var mostrandoFrente = true
    private var acertos = 0
    private var erros = 0

    private var indexAtual = 0
    private lateinit var flashcards: List<Flashcard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFlashcardBinding.inflate(inflater, container, false)

        val scale = requireContext().resources.displayMetrics.density
        binding.cardContainer.cameraDistance = 12000 * scale

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flashcards = args.flashcards.toList()

        atualizarFlashcard()
        atualizarProgresso()

        binding.cardContainer.setOnClickListener { animarFlip() }

        binding.btFechar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAcertei.setOnClickListener {
            acertos++
            binding.textAcertos.text = "Acertos: $acertos"
            irParaProximo()
        }

        binding.btnErrei.setOnClickListener {
            erros++
            binding.textErros.text = "Erros: $erros"
            irParaProximo()
        }

        binding.btnForward.setOnClickListener { irParaProximo() }
        binding.btnBack.setOnClickListener { irParaAnterior() }

    }


    private fun atualizarFlashcard() {
        val fc = flashcards[indexAtual]

        binding.textFrente.text = fc.frente
        binding.textTras.text = fc.verso

        binding.textFrente.visibility = View.VISIBLE
        binding.textTras.visibility = View.GONE

        mostrandoFrente = true
    }

    private fun atualizarProgresso() {
        binding.textProgresso.text = "${indexAtual + 1} / ${flashcards.size}"
    }

    private fun irParaProximo() {
        if (indexAtual < flashcards.size - 1) {
            indexAtual++
            atualizarFlashcard()
            atualizarProgresso()
        }
    }

    private fun irParaAnterior() {
        if (indexAtual > 0) {
            indexAtual--
            atualizarFlashcard()
            atualizarProgresso()
        }
    }

    private fun animarFlip() {

        val viewOut = if (mostrandoFrente) binding.textFrente else binding.textTras
        val viewIn = if (mostrandoFrente) binding.textTras else binding.textFrente

        val animOut = ObjectAnimator.ofFloat(binding.cardContainer, "rotationY", 0f, 90f)
        animOut.duration = 150

        val animIn = ObjectAnimator.ofFloat(binding.cardContainer, "rotationY", -90f, 0f)
        animIn.duration = 150

        animOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                viewOut.visibility = View.GONE
                viewIn.visibility = View.VISIBLE

                mostrandoFrente = !mostrandoFrente
                animIn.start()
            }
        })

        animOut.start()
    }

}
