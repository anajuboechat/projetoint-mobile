package com.projeto.medvest.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentMenuBinding
import com.projeto.medvest.databinding.FragmentNotificacoesBinding

class NotificacoesFragment : Fragment() {
    private var _binding: FragmentNotificacoesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificacoesBinding.inflate(inflater,container,false)
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}