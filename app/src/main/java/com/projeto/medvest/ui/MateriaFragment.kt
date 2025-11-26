package com.projeto.medvest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.projeto.medvest.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.data.DisciplinaComMaterias
import com.projeto.medvest.data.Materia
import com.projeto.medvest.databinding.FragmentMateriaBinding
import com.projeto.medvest.ui.adapter.DisciplinaAdapter
import com.projeto.medvest.ui.adapter.MateriaAdapter

class MateriaFragment : Fragment() {

    private var _binding: FragmentMateriaBinding? = null
    private val binding get() = _binding!!

    private val listaDisciplinas = mutableListOf<DisciplinaComMaterias>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonAdicionarMateria.setOnClickListener {
            findNavController().navigate(R.id.action_materiaFragment_to_criarMateriaFragment)
        }

        binding.recyclerDisciplinas.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        carregarMaterias()
    }

    private fun carregarMaterias() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")

        dbRef.get().addOnSuccessListener { snapshot ->

            listaDisciplinas.clear()

            for (disciplinaNode in snapshot.children) {

                val nomeDisciplina = disciplinaNode.key ?: continue

                val materias = disciplinaNode.children.map { materia ->
                    Materia(
                        nome = materia.key ?: "",
                        disciplina = nomeDisciplina
                    )
                }

                listaDisciplinas.add(
                    DisciplinaComMaterias(nomeDisciplina, materias)
                )
            }

            binding.recyclerDisciplinas.adapter =
                DisciplinaAdapter(listaDisciplinas)

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao carregar matérias", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
