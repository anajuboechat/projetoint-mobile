package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.databinding.FragmentCriarFlashcardBinding

class CriarFlashcardFragment : Fragment() {

    private var _binding: FragmentCriarFlashcardBinding? = null
    private val binding get() = _binding!!

    private val args: CriarFlashcardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSalvar.setOnClickListener {
            salvarFlashcard()
        }
    }

    private fun salvarFlashcard() {
        val frente = binding.editFrente.text.toString().trim()
        val verso = binding.editVerso.text.toString().trim()

        if (frente.isEmpty() || verso.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val disciplina = args.disciplina.lowercase()
        val materia = args.materia.lowercase()

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(disciplina)
            .child(materia)
            .child("flashcards")

        val id = dbRef.push().key ?: return

        val flashcard = mapOf(
            "id" to id,
            "frente" to frente,
            "verso" to verso
        )

        dbRef.child(id).setValue(flashcard)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Flashcard criado!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
