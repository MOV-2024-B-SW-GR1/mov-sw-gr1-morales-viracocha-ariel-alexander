package models

import java.time.LocalDate

data class Zoologico(
    val nombre: String,
    val ubicacion: String,
    val fechaFundacion: LocalDate,
    var capacidadMaxima: Int,
    var presupuestoAnual: Double,
    val animales: MutableList<Animal> = mutableListOf()
) {
    override fun toString(): String {
        val animalesData = animales.joinToString("#") { it.toString() }
        return "$nombre|$ubicacion|$fechaFundacion|$capacidadMaxima|$presupuestoAnual|$animalesData"
    }

    companion object {
        fun fromString(data: String): Zoologico {
            val partes = data.split("|")
            val animales = if (partes.size > 5 && partes[5].isNotEmpty()) {
                partes[5].split("#").map { Animal.fromString(it) }.toMutableList()
            } else {
                mutableListOf()
            }
            return Zoologico(
                partes[0],
                partes[1],
                LocalDate.parse(partes[2]),
                partes[3].toInt(),
                partes[4].toDouble(),
                animales
            )
        }
    }
}

