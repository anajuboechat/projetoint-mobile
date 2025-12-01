package com.projeto.medvest.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.data.DisciplinaComMaterias
import com.projeto.medvest.data.Materia
import com.projeto.medvest.databinding.FragmentMateriaBinding
import com.projeto.medvest.ui.adapter.DisciplinaAdapter
import com.projeto.medvest.util.showBottomSheet

class MateriaFragment : Fragment() {

    private var _binding: FragmentMateriaBinding? = null
    private val binding get() = _binding!!

    private val listaDisciplinas = mutableListOf<DisciplinaComMaterias>()
    private val listaFiltrada = mutableListOf<DisciplinaComMaterias>()
    private lateinit var adapter: DisciplinaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarSearchView()

        binding.recyclerDisciplinas.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        carregarMaterias()

        binding.buttonAdicionarMateria.setOnClickListener {
            val action = MateriaFragmentDirections.actionMateriaFragmentToCriarMateriaFragment()
            findNavController().navigate(action)
        }
    }

    private fun configurarSearchView() {
        val searchView = binding.searchView

        // Remove fundo padrão
        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundResource(0)

        // Ícone da lupa branco
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        // Texto da pesquisa branco
        val searchText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.WHITE)

        searchView.queryHint = "Pesquisar"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarMaterias(newText ?: "")
                return true
            }
        })
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

                val materias = disciplinaNode.children.map { materiaNode ->
                    Materia(
                        nome = materiaNode.key ?: "",
                        disciplina = nomeDisciplina
                    )
                }

                listaDisciplinas.add(DisciplinaComMaterias(nomeDisciplina, materias))
            }

            listaFiltrada.clear()
            listaFiltrada.addAll(listaDisciplinas)

            atualizarAdapter()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao carregar matérias", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarAdapter() {
        adapter = DisciplinaAdapter(
            lista = listaFiltrada,
            onClick = { materia ->
                val action = MateriaFragmentDirections.actionMateriaFragmentToDetalhesMateriaFragment(
                    disciplina = materia.disciplina,
                    materia = materia.nome
                )
                findNavController().navigate(action)
            },
            onDelete = { materia ->
                abrirBottomSheetExcluirMateria(materia)
            }
        )

        binding.recyclerDisciplinas.adapter = adapter
    }

    private fun filtrarMaterias(query: String) {
        val texto = query.lowercase()

        listaFiltrada.clear()

        for (disciplina in listaDisciplinas) {
            val materiasFiltradas = disciplina.materias.filter {
                it.nome.lowercase().contains(texto)
            }

            if (materiasFiltradas.isNotEmpty()) {
                listaFiltrada.add(DisciplinaComMaterias(disciplina.disciplina, materiasFiltradas))
            }
        }

        atualizarAdapter()
    }

    private fun abrirBottomSheetExcluirMateria(materia: Materia) {
        showBottomSheet(
            title = "Excluir Matéria",
            message = "Deseja excluir a matéria \"${materia.nome}\" e todos os seus flashcards?",
            confirmText = "Excluir",
            cancelText = "Cancelar",
            onConfirm = { excluirMateria(materia) }
        )
    }

    private fun excluirMateria(materia: Materia) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(materia.disciplina)
            .child(materia.nome)

        ref.removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Matéria excluída", Toast.LENGTH_SHORT).show()
                carregarMaterias()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao excluir matéria", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
