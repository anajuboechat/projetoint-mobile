package com.projeto.medvest.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentRegisterBinding
import com.projeto.medvest.util.initToolbar
import com.projeto.medvest.util.showBottomSheet
import java.time.Instant

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    binding.progressbar.isVisible = false
                    Toast.makeText(requireContext(), "Falha Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.progressbar.isVisible = false
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        initListeners()
    }

    private fun initListeners() {
        binding.buttonCriarconta.setOnClickListener {
            validateFields()
        }

        binding.buttonGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateFields() {
        val nome = binding.editTextNome.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val senha = binding.editTextSenha.text.toString().trim()
        val senha2 = binding.editTextSenha2.text.toString().trim()

        when {
            nome.isBlank() -> {
                showBottomSheet("Digite seu nome.")
            }

            email.isBlank() -> {
                showBottomSheet(getString(R.string.email_empty_register_fragment))
            }

            senha.isBlank() -> {
                showBottomSheet(getString(R.string.password_empty_register_gragment))
            }

            senha2.isBlank() -> {
                showBottomSheet("Confirme sua senha.")
            }

            !passwordsMatch(senha, senha2) -> {
                binding.editTextSenha2.error = "As senhas não coincidem"
            }

            else -> {
                binding.progressbar.isVisible = true
                registerUser(nome, email, senha)
            }
        }
    }

    private fun passwordsMatch(s1: String, s2: String): Boolean {
        return s1 == s2
    }

    private fun registerUser(nome: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressbar.isVisible = false

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    saveUserToRealtime(
                        uid = uid,
                        email = email,
                        name = nome,   // <<<<<<<<<<<<<<<<<<<<<<<< AQUI ENVIA O NOME
                        avatar = ""
                    )

                    findNavController().navigate(R.id.action_global_homeFragment)

                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        binding.progressbar.isVisible = true
        val intent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(intent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressbar.isVisible = false

                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val email = account.email ?: ""
                    val name = account.displayName ?: ""
                    val avatar = account.photoUrl?.toString() ?: ""

                    saveUserToRealtime(
                        uid = uid,
                        email = email,
                        name = name,   // <<<<<< NOME DO GOOGLE
                        avatar = avatar
                    )

                    findNavController().navigate(R.id.action_global_homeFragment)

                } else {
                    Toast.makeText(requireContext(), "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToRealtime(uid: String, email: String, name: String?, avatar: String?) {

        val data = mapOf(
            "email" to email,
            "nome" to (name ?: ""),        // <<<<  SALVO NO DB
            "avatar" to (avatar ?: ""),
            "ultimoLogin" to Instant.now().toString(),
            "notificacoesFechadas" to false,
            "preferencias" to mapOf(
                "universidades" to emptyList<String>()
            ),
            "resultados_simulados" to emptyList<String>()
        )

        FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(uid)
            .setValue(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
