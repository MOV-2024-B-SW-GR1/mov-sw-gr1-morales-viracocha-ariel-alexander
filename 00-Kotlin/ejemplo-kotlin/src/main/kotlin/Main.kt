package com.app

import java.util.*

// Punto de entrada de la aplicación
fun main(args: Array<String>) {
    // Ejemplo de variable inmutable (val), no se puede reasignar después de inicializar
    val inmutable: String = "Ariel Alexander Morales Viracocha"
    // inmutable = "Otro Nombre" // Error: Val cannot be reassigned
    // Explicación: La palabra clave `val` declara una variable inmutable, por lo que no puede ser reasignada después de la inicialización.

    // Ejemplo de variable mutable (var), se puede reasignar
    var mutable: String = "Morales"
    mutable = "Viracocha" // Esto es válido

    // Duck Typing en Kotlin (el tipo se infiere automáticamente)
    val ejemploVariable = " Ariel Alexander Morales Viracocha "
    ejemploVariable.trim()

    val edadEjemplo: Int = 25
    // ejemploVariable = edadEjemplo // Error: Type mismatch
    // Explicación: Intentar asignar un valor de tipo `Int` a una variable de tipo `String` genera un error, ya que Kotlin no permite cambiar el tipo de una variable después de su declaración.

    // Declaración de variables primitivas
    val nombreProfesor: String = "Ariel Alexander Morales Viracocha"
    val sueldo: Double = 1.2
    val estadoCivil: Char = 'C'
    val mayorEdad: Boolean = true
    val fechaNacimiento: Date = Date()

    // Ejemplo de sentencia when (similar a switch en otros lenguajes)
    val estadoCivilWhen = "C"
    when (estadoCivilWhen) {
        "C" -> println("Casado")
        "S" -> println("Soltero")
        else -> println("No sabemos")
    }

    // Uso de una expresión if para retornar un valor
    val esSoltero = (estadoCivilWhen == "S")
    val coqueteo = if (esSoltero) "Sí" else "No"

    // Llamadas a funciones con parámetros nombrados y opcionales
    imprimirNombre("ArIeL ALeXaNdEr MoRaLeS VIrAcOcHa")
    calcularSueldo(10.00)
    calcularSueldo(10.00, 15.00, 20.00)
    calcularSueldo(10.00, bonoEspecial = 20.00)
    println (calcularSueldo(bonoEspecial = 20.00, sueldo = 10.00, tasa = 14.00));

}

// Función para imprimir el nombre formateado
fun imprimirNombre(nombre: String): Unit { // El tipo Unit es opcional en funciones que no devuelven valor
    // Función interna, disponible solo en este contexto (scope)
    fun otraFuncionAdentro() {
        println("Otra función interna")
    }

    // Uso de template strings en Kotlin
    println("Nombre: $nombre")
    println("Nombre: ${nombre.uppercase()}")
    println("Nombre: $nombre.uppercase()") // Error: No se interpretará el método `uppercase()`
    // Explicación: Para ejecutar métodos dentro de un template string, es necesario usar `${}`.
    // Al no poner las llaves, Kotlin trata el método como texto literal en lugar de ejecutarlo.

    otraFuncionAdentro()
}

// Función para calcular el sueldo con parámetros opcionales y nullables
fun calcularSueldo(
    sueldo: Double, // Parámetro requerido
    tasa: Double = 12.00, // Parámetro opcional con valor por defecto
    bonoEspecial: Double? = null // Parámetro opcional que puede ser nulo
): Double {
    return if (bonoEspecial == null) {
        sueldo * (100 / tasa)
    } else {
        sueldo * (100 / tasa) * bonoEspecial
    }
}

// Clases en Kotlin y ejemplos de constructores primarios y herencia

// Clase base abstracta usando un constructor primario
abstract class Numeros(
    protected val numeroUno: Int, // Atributo de la clase (protected, accesible en subclases)
    protected val numeroDos: Int  // Otro atributo de la clase
) {
    init {
        println("Inicializando la clase Numeros")
    }
}

// Clase derivada, que extiende de la clase base Numeros
class Suma(
    unoParametro: Int,
    dosParametro: Int
) : Numeros(unoParametro, dosParametro) {
    init {
        println("Inicializando la clase Suma")
    }
}

// Ejemplo de uso incorrecto de una clase sin constructor válido
// val sumaInvalida = Suma() // Error: No se han proporcionado los parámetros requeridos
// Explicación: `Suma` requiere dos parámetros en su constructor (unoParametro y dosParametro).
// La creación de un objeto sin estos parámetros no es válida.