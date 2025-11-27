package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.projeto.medvest.databinding.FragmentSimuladosBinding
import androidx.navigation.fragment.findNavController
import com.projeto.medvest.R

class SimuladosFragment : Fragment() {

    private var _binding: FragmentSimuladosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimuladosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navegação para cada simulado
        binding.cardEnem.setOnClickListener {
            // exemplo
            // findNavController().navigate(R.id.action_simuladosFragment_to_simuladoEnemFragment)
        }

        binding.cardUnicamp.setOnClickListener {
            // findNavController().navigate(R.id.action_simuladosFragment_to_simuladoUnicampFragment)
        }

        binding.cardFuvest.setOnClickListener {
            // findNavController().navigate(R.id.action_simuladosFragment_to_simuladoFuvestFragment)
        }

        binding.cardUerj.setOnClickListener {
            // findNavController().navigate(R.id.action_simuladosFragment_to_simuladoUerjFragment)
        }

        binding.btnBackSimulados.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
