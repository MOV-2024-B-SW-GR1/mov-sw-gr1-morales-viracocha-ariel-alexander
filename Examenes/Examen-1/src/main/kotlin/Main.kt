import models.Animal
import models.Zoologico
import services.ZoologicoService
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Scanner

fun main() {
    val zoologicoService = ZoologicoService("zoologicos.txt")
    val scanner = Scanner(System.`in`)

    fun leerEntero(mensaje: String): Int {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return input.toInt()
            } catch (e: NumberFormatException) {
                println("⚠️ Por favor, ingresa un número entero válido.")
            }
        }
    }

    fun leerDouble(mensaje: String): Double {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return input.toDouble()
            } catch (e: NumberFormatException) {
                println("⚠️ Por favor, ingresa un número decimal válido.")
            }
        }
    }

    fun leerFecha(mensaje: String): LocalDate {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine()
            try {
                return LocalDate.parse(input)
            } catch (e: DateTimeParseException) {
                println("⚠️ Por favor, ingresa una fecha válida en formato YYYY-MM-DD.")
            }
        }
    }

    fun leerBooleano(mensaje: String): Boolean {
        while (true) {
            print(mensaje)
            val input = scanner.nextLine().lowercase()
            if (input == "true" || input == "false") {
                return input.toBoolean()
            } else {
                println("⚠️ Por favor, ingresa 'true' o 'false'.")
            }
        }
    }

    while (true) {
        println("\n=========================")
        println("   🐾 Menú Zoológico 🐾")
        println("=========================")
        println("1️⃣  Crear Zoológico")
        println("2️⃣  Ver Zoológicos")
        println("3️⃣  Ver Animales de un Zoológico")
        println("4️⃣  Editar Animal")
        println("5️⃣  Actualizar Zoológico")
        println("6️⃣  Eliminar Zoológico")
        println("7️⃣  Agregar Animal a un Zoológico")
        println("8️⃣  Eliminar Animal de un Zoológico")
        println("9️⃣  Salir")
        print("\nElige una opción: ")

        when (scanner.nextLine().toIntOrNull()) {
            1 -> {
                println("\n--- Crear Zoológico ---")
                print("🌍 Nombre del Zoológico: ")
                val nombre = scanner.nextLine()
                if (nombre.isBlank()) {
                    println("⚠️ El nombre no puede estar vacío.")
                    continue
                }

                print("📍 Ubicación: ")
                val ubicacion = scanner.nextLine()
                if (ubicacion.isBlank()) {
                    println("⚠️ La ubicación no puede estar vacía.")
                    continue
                }

                val fecha = leerFecha("📅 Fecha de fundación (YYYY-MM-DD): ")
                val capacidad = leerEntero("👥 Capacidad máxima: ")
                val presupuesto = leerDouble("💰 Presupuesto anual: ")

                val nuevoZoologico = Zoologico(nombre, ubicacion, fecha, capacidad, presupuesto)
                zoologicoService.crearZoologico(nuevoZoologico)
                println("✅ Zoológico '$nombre' creado exitosamente.")
            }

            2 -> {
                println("\n--- Lista de Zoológicos ---")
                val zoologicos = zoologicoService.leerZoologicos()
                if (zoologicos.isEmpty()) {
                    println("⚠️ No hay zoológicos registrados.")
                } else {
                    zoologicos.forEachIndexed { index, zoo ->
                        println("\n=== Zoológico ${index + 1} ===")
                        println("🌍 Nombre: ${zoo.nombre}")
                        println("📍 Ubicación: ${zoo.ubicacion}")
                        println("📅 Fecha de fundación: ${zoo.fechaFundacion}")
                        println("👥 Capacidad máxima: ${zoo.capacidadMaxima}")
                        println("💰 Presupuesto anual: ${zoo.presupuestoAnual}")
                    }
                }
            }

            3 -> {
                println("\n--- Ver Animales de un Zoológico ---")
                print("🌍 Nombre del Zoológico: ")
                val nombreZoologico = scanner.nextLine()
                val zoologico = zoologicoService.leerZoologicos().find { it.nombre == nombreZoologico }
                if (zoologico == null) {
                    println("⚠️ Zoológico '$nombreZoologico' no encontrado.")
                } else if (zoologico.animales.isEmpty()) {
                    println("⚠️ El zoológico '$nombreZoologico' no tiene animales registrados.")
                } else {
                    println("\n🐾 Animales en el zoológico '$nombreZoologico':")
                    zoologico.animales.forEach { animal ->
                        println("  📛 Nombre: ${animal.nombre}")
                        println("  🐾 Especie: ${animal.especie}")
                        println("  🎂 Edad: ${animal.edad} años")
                        println("  ⚖️ Peso: ${animal.peso} kg")
                        println("  ⚠️ En peligro: ${if (animal.enPeligro) "Sí" else "No"}\n")
                    }
                }
            }

            4 -> {
                println("\n--- Editar Animal ---")
                print("🌍 Nombre del Zoológico: ")
                val nombreZoologico = scanner.nextLine()
                val zoologico = zoologicoService.leerZoologicos().find { it.nombre == nombreZoologico }
                if (zoologico == null) {
                    println("⚠️ Zoológico '$nombreZoologico' no encontrado.")
                    continue
                }

                print("📛 Nombre del Animal a editar: ")
                val nombreAnimal = scanner.nextLine()
                val animal = zoologico.animales.find { it.nombre == nombreAnimal }
                if (animal == null) {
                    println("⚠️ Animal '$nombreAnimal' no encontrado en el zoológico '$nombreZoologico'.")
                    continue
                }

                println("\n--- Editar información del animal ---")
                println("1️⃣ Edad")
                println("2️⃣ Peso")
                println("3️⃣ En peligro de extinción")
                print("Elige una opción para editar: ")

                when (scanner.nextLine().toIntOrNull()) {
                    1 -> {
                        val nuevaEdad = leerEntero("🎂 Nueva edad: ")
                        zoologicoService.actualizarAnimal(nombreZoologico, nombreAnimal, nuevaEdad, null, null)
                    }
                    2 -> {
                        val nuevoPeso = leerDouble("⚖️ Nuevo peso: ")
                        zoologicoService.actualizarAnimal(nombreZoologico, nombreAnimal, null, nuevoPeso, null)
                    }
                    3 -> {
                        val enPeligro = leerBooleano("⚠️ ¿Está en peligro de extinción? (true/false): ")
                        zoologicoService.actualizarAnimal(nombreZoologico, nombreAnimal, null, null, enPeligro)
                    }
                    else -> println("Opción inválida.")
                }
                println("✅ Animal '$nombreAnimal' actualizado exitosamente.")
            }


            5 -> {
                println("\n--- Actualizar Zoológico ---")
                print("🌍 Nombre del Zoológico a actualizar: ")
                val nombre = scanner.nextLine()
                val zoologico = zoologicoService.leerZoologicos().find { it.nombre == nombre }
                if (zoologico == null) {
                    println("⚠️ Zoológico '$nombre' no encontrado.")
                    continue
                }

                val capacidad = leerEntero("👥 Nueva capacidad máxima: ")
                val presupuesto = leerDouble("💰 Nuevo presupuesto anual: ")

                zoologicoService.actualizarZoologico(nombre, capacidad, presupuesto)
                println("✅ Zoológico '$nombre' actualizado exitosamente.")
            }

            6 -> {
                println("\n--- Eliminar Zoológico ---")
                print("🌍 Nombre del Zoológico a eliminar: ")
                val nombre = scanner.nextLine()
                if (zoologicoService.leerZoologicos().none { it.nombre == nombre }) {
                    println("⚠️ Zoológico '$nombre' no encontrado.")
                } else {
                    zoologicoService.eliminarZoologico(nombre)
                    println("✅ Zoológico '$nombre' eliminado exitosamente.")
                }
            }

            7 -> {
                println("\n--- Agregar Animal ---")
                print("🌍 Nombre del Zoológico: ")
                val nombreZoologico = scanner.nextLine()
                val zoologico = zoologicoService.leerZoologicos().find { it.nombre == nombreZoologico }
                if (zoologico == null) {
                    println("⚠️ Zoológico '$nombreZoologico' no encontrado.")
                    continue
                }

                print("📛 Nombre del Animal: ")
                val nombreAnimal = scanner.nextLine()
                print("🐾 Especie: ")
                val especie = scanner.nextLine()
                val edad = leerEntero("🎂 Edad: ")
                val peso = leerDouble("⚖️ Peso: ")
                val enPeligro = leerBooleano("⚠️ ¿Está en peligro de extinción? (true/false): ")

                val nuevoAnimal = Animal(nombreAnimal, especie, edad, peso, enPeligro)
                zoologicoService.agregarAnimal(nombreZoologico, nuevoAnimal)
                println("Animal '$nombreAnimal' agregado exitosamente al zoológico '$nombreZoologico'.")
            }

            8 -> {
                println("\n--- Eliminar Animal ---")
                print("Nombre del Zoológico: ")
                val nombreZoologico = scanner.nextLine()
                val zoologico = zoologicoService.leerZoologicos().find { it.nombre == nombreZoologico }
                if (zoologico == null) {
                    println("⚠️ Zoológico '$nombreZoologico' no encontrado.")
                    continue
                }

                print("📛 Nombre del Animal a eliminar: ")
                val nombreAnimal = scanner.nextLine()
                if (zoologico.animales.none { it.nombre == nombreAnimal }) {
                    println("⚠️ Animal '$nombreAnimal' no encontrado en el zoológico '$nombreZoologico'.")
                } else {
                    zoologicoService.eliminarAnimal(nombreZoologico, nombreAnimal)
                    println("Animal '$nombreAnimal' eliminado exitosamente.")
                }
            }


            9 -> {
                println("👋 ¡Hasta pronto!")
                break
            }

            else -> println("⚠️ Opción inválida. Por favor, intenta de nuevo.")
        }
    }
}



