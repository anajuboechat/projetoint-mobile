package com.projeto.medvest.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projeto.medvest.ui.MenuFragment
import com.projeto.medvest.ui.EstudosFragment
import com.projeto.medvest.ui.NotificacoesFragment
import com.projeto.medvest.ui.profile.ProfileFragment

class ViewTaskAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MenuFragment()
            1 -> EstudosFragment()
            2 -> NotificacoesFragment()
            3 -> ProfileFragment()
            else -> MenuFragment()
        }
    }
}
