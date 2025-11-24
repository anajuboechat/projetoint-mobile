package com.projeto.medvest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentFlashcardBinding
import com.projeto.medvest.databinding.FragmentMenuBinding

class FlashcardFragment : Fragment() {
    private var _binding: FragmentFlashcardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlashcardBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}