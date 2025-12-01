package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.projeto.medvest.R
import com.projeto.medvest.data.DisciplinaComMaterias
import com.projeto.medvest.data.Materia
import com.projeto.medvest.databinding.FragmentMateriaBinding
import com.projeto.medvest.ui.adapter.DisciplinaAdapter

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

        // Toolbar
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.toolbar.inflateMenu(R.menu.menu_search)

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Pesquisar matéria"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarMaterias(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarMaterias(newText ?: "")
                return true
            }
        })

        // RecyclerView
        binding.recyclerDisciplinas.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val spacingInDp = 40
        val spacingInPx = (spacingInDp * resources.displayMetrics.density).toInt()

        binding.recyclerDisciplinas.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = spacingInPx
            }
        })

        adapter = DisciplinaAdapter(listaFiltrada) { materia ->
            val action = MateriaFragmentDirections
                .actionMateriaFragmentToDetalhesMateriaFragment(
                    disciplina = materia.disciplina,
                    materia = materia.nome
                )
            findNavController().navigate(action)
        }
        binding.recyclerDisciplinas.adapter = adapter

        binding.buttonAdicionarMateria.setOnClickListener {
            val action = MateriaFragmentDirections
                .actionMateriaFragmentToCriarMateriaFragment()
            findNavController().navigate(action)
        }

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

                val materias = disciplinaNode.children.map { materiaNode ->
                    Materia(
                        nome = materiaNode.key ?: "",
                        disciplina = nomeDisciplina
                    )
                }

                listaDisciplinas.add(
                    DisciplinaComMaterias(
                        disciplina = nomeDisciplina,
                        materias = materias
                    )
                )
            }

            // Inicializar lista filtrada
            listaFiltrada.clear()
            listaFiltrada.addAll(listaDisciplinas)
            adapter.notifyDataSetChanged()

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao carregar matérias", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filtrarMaterias(query: String) {
        val texto = query.lowercase()
        listaFiltrada.clear()

        for (disciplina in listaDisciplinas) {
            val materiasFiltradas = disciplina.materias.filter {
                it.nome.lowercase().contains(texto)
            }

            if (materiasFiltradas.isNotEmpty()) {
                listaFiltrada.add(
                    DisciplinaComMaterias(
                        disciplina = disciplina.disciplina,
                        materias = materiasFiltradas
                    )
                )
            }
        }

        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}