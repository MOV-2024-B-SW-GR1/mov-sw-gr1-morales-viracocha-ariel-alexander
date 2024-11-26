package services

import models.Animal
import models.Zoologico
import utils.FileManager
import java.time.LocalDate

class ZoologicoService(private val archivo: String) {

    fun crearZoologico(zoologico: Zoologico) {
        val zoologicos = leerZoologicos().toMutableList()
        zoologicos.add(zoologico)
        guardarZoologicos(zoologicos)
    }

    fun leerZoologicos(): List<Zoologico> {
        return FileManager.leerArchivo(archivo).map { Zoologico.fromString(it) }
    }

    fun actualizarZoologico(nombre: String, capacidadMaxima: Int?, presupuestoAnual: Double?) {
        val zoologicos = leerZoologicos().toMutableList()
        val index = zoologicos.indexOfFirst { it.nombre == nombre }
        if (index != -1) {
            capacidadMaxima?.let { zoologicos[index].capacidadMaxima = it }
            presupuestoAnual?.let { zoologicos[index].presupuestoAnual = it }
            guardarZoologicos(zoologicos)
        }
    }

    fun eliminarZoologico(nombre: String) {
        val zoologicos = leerZoologicos().filter { it.nombre != nombre }
        guardarZoologicos(zoologicos)
    }

    fun agregarAnimal(nombreZoologico: String, animal: Animal) {
        val zoologicos = leerZoologicos().toMutableList()
        val index = zoologicos.indexOfFirst { it.nombre == nombreZoologico }
        if (index != -1) {
            zoologicos[index].animales.add(animal)
            guardarZoologicos(zoologicos)
        }
    }

    fun eliminarAnimal(nombreZoologico: String, nombreAnimal: String) {
        val zoologicos = leerZoologicos().toMutableList()
        val index = zoologicos.indexOfFirst { it.nombre == nombreZoologico }
        if (index != -1) {
            zoologicos[index].animales.removeIf { it.nombre == nombreAnimal }
            guardarZoologicos(zoologicos)
        }
    }
    fun actualizarAnimal(nombreZoologico: String, nombreAnimal: String, nuevaEdad: Int?, nuevoPeso: Double?, enPeligro: Boolean?) {
        val zoologicos = leerZoologicos().toMutableList()
        val zoologicoIndex = zoologicos.indexOfFirst { it.nombre == nombreZoologico }
        if (zoologicoIndex != -1) {
            val animales = zoologicos[zoologicoIndex].animales
            val animalIndex = animales.indexOfFirst { it.nombre == nombreAnimal }
            if (animalIndex != -1) {
                nuevaEdad?.let { animales[animalIndex].edad = it }
                nuevoPeso?.let { animales[animalIndex].peso = it }
                enPeligro?.let { animales[animalIndex].enPeligro = it }
                guardarZoologicos(zoologicos)
            }
        }
    }


    private fun guardarZoologicos(zoologicos: List<Zoologico>) {
        FileManager.escribirArchivo(archivo, zoologicos.map { it.toString() })
    }
}
