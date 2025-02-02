package utils

import java.io.File

object FileManager {
    fun leerArchivo(nombreArchivo: String): List<String> {
        val archivo = File(nombreArchivo)
        if (!archivo.exists()) archivo.createNewFile()
        return archivo.readLines()
    }

    fun escribirArchivo(nombreArchivo: String, datos: List<String>) {
        File(nombreArchivo).writeText(datos.joinToString("\n"))
    }
}
