package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.projeto.medvest.databinding.FragmentSimuladosBinding
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
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

        binding.cardEnem.setOnClickListener {
            val action = SimuladosFragmentDirections
                .actionSimuladosFragmentToListaSimuladosFragment("enem")
            findNavController().navigate(action)
        }

        binding.cardUnicamp.setOnClickListener {
            val action = SimuladosFragmentDirections
                .actionSimuladosFragmentToListaSimuladosFragment("unicamp")
            findNavController().navigate(action)
        }

        binding.cardFuvest.setOnClickListener {
            val action = SimuladosFragmentDirections
                .actionSimuladosFragmentToListaSimuladosFragment("fuvest")
            findNavController().navigate(action)
        }

        binding.cardUerj.setOnClickListener {
            val action = SimuladosFragmentDirections
                .actionSimuladosFragmentToListaSimuladosFragment("uerj")
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
