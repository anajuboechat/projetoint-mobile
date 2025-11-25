package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.databinding.FragmentCriarMateriaBinding

class CriarMateriaFragment : Fragment() {

    private var _binding: FragmentCriarMateriaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCriarMateriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Opções para o dropdown
        val disciplinas = listOf("Física", "Química", "Biologia")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            disciplinas
        )
        binding.dropdownDisciplina.setAdapter(adapter)

        // botão voltar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // botão salvar
        binding.SalvarMateria.setOnClickListener {
            salvarMateria()
        }
    }

    private fun salvarMateria() {
        val nome = binding.edittextnome.text.toString().trim()
        val disciplina = binding.dropdownDisciplina.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(requireContext(), "Digite um nome para a matéria", Toast.LENGTH_SHORT).show()
            return
        }

        if (disciplina.isEmpty()) {
            Toast.makeText(requireContext(), "Escolha uma disciplina", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "Erro: usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // ✔ Caminho CORRETO baseado nas regras do Firebase
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(disciplina.lowercase())

        // ✔ cria um novo ID
        val key = dbRef.push().key ?: return

        val materiaMap = mapOf(
            "idMateria" to key,
            "nome" to nome,
            "disciplina" to disciplina
        )

        // ✔ salva no nó correto: usuarios/uid/categoria/disciplina/<key>
        dbRef.child(key).setValue(materiaMap)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Matéria criada com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
