package com.projeto.medvest.data

data class Notification(
    var id: Int = 0,
    var idUser: String = "",
    var mensagem: String = "",
    var data: String = "",
    var icon: Long = 1L
) {
    constructor() : this(0, "", "", "", 1L)
}
