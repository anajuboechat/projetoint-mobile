package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projeto.medvest.databinding.FragmentHomeBinding
import com.projeto.medvest.util.showBottomSheet
import com.projeto.medvest.R
import com.projeto.medvest.ui.profile.NotificacoesFragment
import com.projeto.medvest.ui.profile.ProfileFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupNavBar()
        initListeners() // IMPORTANTE: agora os listeners realmente são chamados
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 4
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MenuFragment()
                    1 -> EstudosFragment()
                    2 -> NotificacoesFragment()
                    else -> ProfileFragment()
                }
            }
        }

        binding.viewPager.isUserInputEnabled = false
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            showBottomSheet(
                title = getString(R.string.text_title_dialog_confirm_logout),
                message = getString(R.string.text_message_dialog_confirm_logout),
                confirmText = getString(R.string.text_button_dialog_confirm_logout),
                cancelText = getString(R.string.text_button_dialog_cancel),
                onConfirm = {
                    findNavController().navigate(R.id.action_homeFragment_to_autentication)
                }
            )
        }
    }

    private fun setupNavBar() {
        binding.navHome.setOnClickListener { binding.viewPager.currentItem = 0 }
        binding.navSimulados.setOnClickListener { binding.viewPager.currentItem = 1 }
        binding.navNotificacoes.setOnClickListener { binding.viewPager.currentItem = 2 }
        binding.navPerfil.setOnClickListener { binding.viewPager.currentItem = 3 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
