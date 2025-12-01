package com.projeto.medvest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.projeto.medvest.databinding.ActivityMainBinding
import com.projeto.medvest.util.showBottomSheet

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNav()
        initListeners()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                // TELAS QUE ESCONDEM TUDO
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.recoverAccountFragment, -> {
                    binding.navBar.visibility = View.GONE
                    binding.btnBack.visibility = View.GONE
                    binding.btnLogout.visibility = View.GONE
                }

                R.id.profileFragment,
                R.id.menuFragment,
                R.id.notificacoesFragment -> {
                    binding.navBar.visibility = View.VISIBLE
                    binding.btnBack.visibility = View.GONE
                    binding.btnLogout.visibility = View.VISIBLE
                }

                // OUTRAS TELAS
                else -> {
                    binding.navBar.visibility = View.VISIBLE
                    binding.btnBack.visibility = View.VISIBLE
                    binding.btnLogout.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun initListeners() {
        // Botão voltar
        binding.btnBack.setOnClickListener {
            navController.navigateUp()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            showBottomSheetLogout()
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            navController.navigate(R.id.menuFragment)
        }
        binding.navSimulados.setOnClickListener {
            navController.navigate(R.id.estudosFragment)
        }
        binding.navNotificacoes.setOnClickListener {
            navController.navigate(R.id.notificacoesFragment)
        }
        binding.navPerfil.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }
    }

    fun showBottomSheetLogout() {
        val title = getString(R.string.text_title_dialog_confirm_logout)
        val message = getString(R.string.text_message_dialog_confirm_logout)
        val confirm = getString(R.string.text_button_dialog_confirm_logout)
        val cancel = getString(R.string.text_button_dialog_cancel)

        val currentFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.firstOrNull()

        currentFragment?.showBottomSheet(
            title = title,
            message = message,
            confirmText = confirm,
            cancelText = cancel,
            onConfirm = {
                navController.navigate(R.id.action_homeFragment_to_autentication)
            }
        )
    }
}
