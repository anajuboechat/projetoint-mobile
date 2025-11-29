package com.projeto.medvest.ui.model

data class SimuladoQuestao(
    val numero: Int = 0,
    val enunciado: String = "",
    val alternativas: List<String> = emptyList(),
    val correta: String = "",
    var respostaUsuario: String? = null
)