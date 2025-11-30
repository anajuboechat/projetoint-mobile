package com.projeto.medvest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.projeto.medvest.data.Flashcard
import com.projeto.medvest.databinding.FragmentDetalhesMateriaBinding
import com.projeto.medvest.ui.adapter.FlashcardAdapter
import com.projeto.medvest.R

class DetalhesMateriaFragment : Fragment() {

    private val args: DetalhesMateriaFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetalhesMateriaBinding

    private lateinit var adapter: FlashcardAdapter
    private val flashcards = mutableListOf<Flashcard>()   // lista original
    private var listaVisivel = mutableListOf<Flashcard>() // lista filtrada

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalhesMateriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val disciplinaKey = args.disciplina   // ❗ sem lowercase
        val materiaKey = args.materia         // ❗ sem lowercase

        // Botão voltar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Toolbar com search
        binding.toolbar.inflateMenu(R.menu.menu_search)
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Pesquisar flashcard"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarFlashcards(newText ?: "")
                return true
            }
        })

        // Títulos
        binding.tituloDisciplina.text = args.disciplina
        binding.tituloMateria.text = args.materia

        // Adapter
        adapter = FlashcardAdapter(listaVisivel) { abrirFlashcards() }

        binding.recyclerFlashcards.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerFlashcards.adapter = adapter

        // Carregar flashcards
        carregarFlashcards(disciplinaKey, materiaKey)

        // Criar novo flashcard
        binding.btCriarFlashcard.setOnClickListener {
            val action = DetalhesMateriaFragmentDirections
                .actionDetalhesMateriaFragmentToCriarFlashcardFragment(
                    args.disciplina,
                    args.materia
                )
            findNavController().navigate(action)
        }
    }

    // FILTRAGEM FUNCIONANDO
    private fun filtrarFlashcards(texto: String) {
        listaVisivel = if (texto.isEmpty()) {
            flashcards.toMutableList()
        } else {
            flashcards.filter { f ->
                f.frente.contains(texto, ignoreCase = true) ||
                        f.verso.contains(texto, ignoreCase = true)
            }.toMutableList()
        }

        adapter.atualizarLista(listaVisivel)
    }

    // CARREGAMENTO DO FIREBASE
    private fun carregarFlashcards(disciplina: String, materia: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val flashcardsRef = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(disciplina)
            .child(materia)
            .child("flashcards")

        flashcardsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d("DEBUG_SNAPSHOT", snapshot.value?.toString() ?: "snapshot vazio")

                flashcards.clear()

                for (item in snapshot.children) {
                    item.getValue(Flashcard::class.java)?.let { flashcards.add(it) }
                }

                listaVisivel = flashcards.toMutableList()
                adapter.atualizarLista(listaVisivel)

                binding.textSemFlashcards.visibility =
                    if (flashcards.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ABRIR FLASHCARDS NA ORDEM CORRETA
    private fun abrirFlashcards() {
        val action = DetalhesMateriaFragmentDirections
            .actionDetalhesMateriaFragmentToFlashcardFragment(
                listaVisivel.toTypedArray()
            )
        findNavController().navigate(action)
    }
}
