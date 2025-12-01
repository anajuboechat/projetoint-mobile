package com.projeto.medvest.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.projeto.medvest.util.showBottomSheet

class DetalhesMateriaFragment : Fragment() {

    private val args: DetalhesMateriaFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetalhesMateriaBinding

    private lateinit var adapter: FlashcardAdapter
    private val flashcards = mutableListOf<Flashcard>()
    private var listaVisivel = mutableListOf<Flashcard>()

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

        val disciplinaKey = args.disciplina
        val materiaKey = args.materia

        // Configura SearchView
        configurarSearchView()

        binding.tituloDisciplina.text = disciplinaKey
        binding.tituloMateria.text = materiaKey

        adapter = FlashcardAdapter(listaVisivel) { flashcard ->
            abrirBottomSheetExcluir(flashcard)
        }

        binding.recyclerFlashcards.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerFlashcards.adapter = adapter

        carregarFlashcards(disciplinaKey, materiaKey)

        binding.btCriarFlashcard.setOnClickListener {
            val action = DetalhesMateriaFragmentDirections
                .actionDetalhesMateriaFragmentToCriarFlashcardFragment(
                    disciplinaKey,
                    materiaKey
                )
            findNavController().navigate(action)
        }
    }

    private fun configurarSearchView() {
        val searchView = binding.searchView2

        // Define hint
        searchView.queryHint = "Pesquisar flashcard"

        // Mostra teclado e campo expandido
        searchView.isIconified = false

        // Altera cor do ícone de lupa
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(requireContext().getColor(com.projeto.medvest.R.color.palavras), PorterDuff.Mode.SRC_IN)

        // Listener de pesquisa
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarFlashcards(newText ?: "")
                return true
            }
        })
    }

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

    private fun abrirBottomSheetExcluir(flashcard: Flashcard) {
        showBottomSheet(
            title = "Excluir Flashcard",
            message = "Tem certeza que deseja excluir este flashcard?",
            confirmText = "Excluir",
            cancelText = "Cancelar",
            onConfirm = {
                deletarFlashcard(flashcard)
            }
        )
    }

    private fun deletarFlashcard(flashcard: Flashcard) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .child("categoria")
            .child(args.disciplina)
            .child(args.materia)
            .child("flashcards")
            .child(flashcard.id)

        ref.removeValue()
    }
}
