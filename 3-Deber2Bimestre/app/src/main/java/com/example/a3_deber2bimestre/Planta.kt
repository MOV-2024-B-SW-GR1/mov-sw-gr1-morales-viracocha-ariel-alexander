package com.example.a3_deber2bimestre

data class Planta(
    val id: Int,
    var especie: String,
    var edad: Int,
    var color: String,
    var altura: Double,
    val parcelaId: Int  // Para relacionar la planta con su parcela
)