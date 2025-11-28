package com.projeto.medvest.ui

import android.os.Bundle
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
    private val flashcards = mutableListOf<Flashcard>()

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

        val disciplinaKey = args.disciplina.lowercase()
        val materiaKey = args.materia.lowercase()

        // 🔙 Botão de voltar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // 📌 Inflar menu de busca
        binding.toolbar.inflateMenu(R.menu.menu_search)

        // 🔍 Configurar barra de busca
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

        // 🔠 Títulos
        binding.tituloDisciplina.text = args.disciplina
        binding.tituloMateria.text = args.materia

        // 📚 Recycler + Adapter
        adapter = FlashcardAdapter(mutableListOf()) { flashcard ->
            abrirFlashcard(flashcard)
        }

        binding.recyclerFlashcards.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerFlashcards.adapter = adapter

        // 🔄 Carregar dados do Firebase
        carregarFlashcards(disciplinaKey, materiaKey)

        // ➕ Criar flashcard
        binding.btCriarFlashcard.setOnClickListener {
            val action = DetalhesMateriaFragmentDirections
                .actionDetalhesMateriaFragmentToCriarFlashcardFragment(
                    args.disciplina,
                    args.materia
                )
            findNavController().navigate(action)
        }
    }

    // 🔍 Filtrar lista
    private fun filtrarFlashcards(texto: String) {
        val listaFiltrada = flashcards.filter { f ->
            f.frente.contains(texto, ignoreCase = true) ||
                    f.verso.contains(texto, ignoreCase = true)
        }
        adapter.atualizarLista(listaFiltrada)
    }

    // 🔄 Carregar do Firebase
    private fun carregarFlashcards(disciplina: String, materia: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(disciplina)
            .child(materia)
            .child("flashcards")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                flashcards.clear()

                for (item in snapshot.children) {
                    val flashcard = item.getValue(Flashcard::class.java)
                    if (flashcard != null) flashcards.add(flashcard)
                }

                adapter.atualizarLista(flashcards)

                binding.textSemFlashcards.visibility =
                    if (flashcards.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun abrirFlashcard(flashcard: Flashcard) {
        val action = DetalhesMateriaFragmentDirections
            .actionDetalhesMateriaFragmentToFlashcardFragment(flashcard)
        findNavController().navigate(action)
    }
}
