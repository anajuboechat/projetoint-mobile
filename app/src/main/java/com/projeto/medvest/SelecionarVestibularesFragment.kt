package com.projeto.medvest.ui.vestibulares

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.projeto.medvest.MainActivity
import com.projeto.medvest.R
import com.projeto.medvest.ui.adapter.VestibularAdapter
import com.projeto.medvest.ui.model.Vestibular

class SelecionarVestibularesFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_selecionar_vestibulares, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerVestibulares)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val lista = listOf(
            Vestibular("ENEM", R.drawable.enem),
            Vestibular("FUVEST", R.drawable.usp),
            Vestibular("UNICAMP", R.drawable.unicamp2),
            Vestibular("UERJ", R.drawable.uerj)
        )

        val adapter = VestibularAdapter(lista) {}
        recycler.adapter = adapter

        view.findViewById<Button>(R.id.btnContinuar).setOnClickListener {
            findNavController().navigate(R.id.action_global_menuFragment) // tentar tirar dps
            val selecionados = adapter.getSelecionados().toList()

            Log.d("DEBUG", "Selecionados = $selecionados") // <-- AQUI

            if (selecionados.isEmpty()) {
                println("Nenhum vestibular selecionado")
                return@setOnClickListener
            }

            salvarPreferencias(selecionados)
        }
    }

    private fun salvarPreferencias(universidades: List<String>) {
        val uid = auth.currentUser?.uid ?: return

        val prefs = mapOf("universidades" to universidades)

        firestore.collection("users")
            .document(uid)
            .collection("preferencias")
            .document("vestibulares")
            .set(prefs)
            .addOnSuccessListener {
                println("Preferências salvas!")

                findNavController().navigate(R.id.action_global_menuFragment)

            }
            .addOnFailureListener {
                println("Erro ao salvar: ${it.message}")
            }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBars()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBars()
    }
}