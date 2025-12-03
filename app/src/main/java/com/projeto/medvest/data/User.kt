package com.projeto.medvest.data

data class User(
    val uid: String = "",
    val email: String = "",
    val nome: String = "",
    val avatar: String = "",
    val ultimoLogin: String = "",
    val notificacoesFechadas: Boolean = false,
    val preferencias: Preferencias = Preferencias(),
    val resultados_simulados: List<String> = emptyList()
)

data class Preferencias(
    val universidades: List<String> = emptyList()
)
