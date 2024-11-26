package models

data class Animal(
    val nombre: String,
    val especie: String,
    var edad: Int,
    var peso: Double,
    var enPeligro: Boolean
) {
    override fun toString(): String {
        return "$nombre,$especie,$edad,$peso,$enPeligro"
    }

    companion object {
        fun fromString(data: String): Animal {
            val partes = data.split(",")
            return Animal(
                partes[0],
                partes[1],
                partes[2].toInt(),
                partes[3].toDouble(),
                partes[4].toBoolean()
            )
        }
    }
}
