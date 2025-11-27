package com.projeto.medvest.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference.child("usuarios")

    private var selectedBitmap: Bitmap? = null

    // Abrir galeria
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                val bitmap = uriToBitmap(uri)

                if (bitmap != null) {
                    selectedBitmap = reduzirBitmap(bitmap)
                    binding.imageProfile.setImageBitmap(selectedBitmap)

                    salvarImagem(selectedBitmap!!)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = auth.currentUser!!.uid

        carregarDados(uid)

        binding.iconEditProfile.setOnClickListener {
            abrirGaleria()
        }

        binding.buttonCriarconta.setOnClickListener {
            salvarAlteracoes(uid)
        }

        binding.textViewLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_global_authentication)
        }
    }

    // ------------------------------------------------------------------
    // ------------------------ CARREGAR DADOS ---------------------------
    // ------------------------------------------------------------------
    private fun carregarDados(uid: String) {

        database.child(uid).get().addOnSuccessListener { snap ->
            if (!snap.exists()) return@addOnSuccessListener

            binding.editTextNome.setText(snap.child("nome").value?.toString() ?: "")
            binding.editTextEmail.setText(snap.child("email").value?.toString() ?: "")

            // Carregar avatar
            val avatarBase64 = snap.child("avatar").value?.toString()
            if (avatarBase64 != null && avatarBase64.isNotBlank()) {

                val pureBase64 = avatarBase64.substringAfter("base64,", "")

                val bitmap = base64ToBitmap(pureBase64)
                if (bitmap != null) {
                    binding.imageProfile.setImageBitmap(bitmap)
                }
            }

            val listaVest = snap.child("vestibulares").children.mapNotNull { it.getValue(String::class.java) }

            binding.cbEnem.isChecked = listaVest.any { it.equals("enem", ignoreCase = true) }
            binding.cbComvest.isChecked = listaVest.any { it.equals("comvest", ignoreCase = true) }
            binding.cbFuvest.isChecked = listaVest.any { it.equals("fuvest", ignoreCase = true) }
            binding.cbOutro.isChecked = listaVest.any { it.equals("outro", ignoreCase = true) }
        }
    }

    // ------------------------------------------------------------------
    // ------------------------- SALVAR IMAGEM ---------------------------
    // ------------------------------------------------------------------
    private fun salvarImagem(bitmap: Bitmap) {

        val base64 = bitmapToBase64(bitmap)

        // prefixo IGUAL ao JavaScript
        val fullBase64 = "data:image/png;base64,$base64"

        val uid = auth.currentUser!!.uid

        database.child(uid).child("avatar").setValue(fullBase64)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Foto atualizada!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar foto!", Toast.LENGTH_SHORT).show()
            }
    }

    // ------------------------------------------------------------------
    // ----------------------- SALVAR DEMAIS CAMPOS ----------------------
    // ------------------------------------------------------------------
    private fun salvarAlteracoes(uid: String) {

        val nome = binding.editTextNome.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val vestibulares = mutableListOf<String>()
        if (binding.cbEnem.isChecked) vestibulares.add("enem")
        if (binding.cbComvest.isChecked) vestibulares.add("comvest")
        if (binding.cbFuvest.isChecked) vestibulares.add("fuvest")
        if (binding.cbOutro.isChecked) vestibulares.add("outro")

        val updates = mapOf(
            "nome" to nome,
            "email" to email,
            "vestibulares" to vestibulares
        )

        database.child(uid).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Perfil salvo!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show()
            }
    }

    // ------------------------------------------------------------------
    // --------------------------- IMAGENS -------------------------------
    // ------------------------------------------------------------------
    private fun abrirGaleria() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Intent(MediaStore.ACTION_PICK_IMAGES)
        else
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        galleryLauncher.launch(intent)
    }

    private fun uriToBitmap(uri: Uri?): Bitmap? {
        return try {
            requireActivity().contentResolver.openInputStream(uri!!).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun reduzirBitmap(bitmap: Bitmap, largura: Int = 600): Bitmap {
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val altura = (largura / ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, largura, altura, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    private fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
