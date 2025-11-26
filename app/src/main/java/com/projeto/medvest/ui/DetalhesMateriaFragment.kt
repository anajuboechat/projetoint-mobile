package com.projeto.medvest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.projeto.medvest.data.Flashcard
import com.projeto.medvest.databinding.FragmentDetalhesMateriaBinding
import com.projeto.medvest.ui.adapter.FlashcardAdapter

class DetalhesMateriaFragment : Fragment() {

    private val args: DetalhesMateriaFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetalhesMateriaBinding

    private lateinit var adapter: FlashcardAdapter
    private val flashcards = mutableListOf<Flashcard>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetalhesMateriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val disciplina = args.disciplina.lowercase()
        val materia = args.materia.lowercase()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.tituloMateria.text = args.materia
        binding.tituloDisciplina.text = args.disciplina

        // RecyclerView
        adapter = FlashcardAdapter(flashcards)
        binding.recyclerFlashcards.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFlashcards.adapter = adapter

        carregarFlashcards(disciplina, materia)

        // botão para criar flashcard
        binding.btCriarFlashcard.setOnClickListener {
            val action = DetalhesMateriaFragmentDirections
                .actionDetalhesMateriaFragmentToCriarFlashcardFragment(args.disciplina, args.materia)

            findNavController().navigate(action)
        }
    }

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
                    if (flashcard != null) {
                        flashcards.add(flashcard)
                    }
                }

                adapter.notifyDataSetChanged()

                binding.textSemFlashcards.visibility =
                    if (flashcards.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
