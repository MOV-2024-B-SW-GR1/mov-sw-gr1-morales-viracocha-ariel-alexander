package com.example.a3_deber2bimestre

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL("PRAGMA foreign_keys = ON;")
            db?.execSQL(CREATE_TABLE_PARCELA)
            db?.execSQL(CREATE_TABLE_PLANTA)
            db?.execSQL(CREATE_TABLE_ALARMA)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALARMA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLANTA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PARCELA")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "ParcelasDB"
        const val DATABASE_VERSION = 6 // Versión incrementada

        // Tabla Parcela
        const val TABLE_PARCELA = "Parcela"
        const val COLUMN_PARCELA_ID = "id"
        const val COLUMN_PARCELA_NOMBRE = "nombre"

        private const val CREATE_TABLE_PARCELA = """
            CREATE TABLE $TABLE_PARCELA (
                $COLUMN_PARCELA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PARCELA_NOMBRE TEXT NOT NULL UNIQUE
            )
        """

        // Tabla Planta
        const val TABLE_PLANTA = "Planta"
        const val COLUMN_PLANTA_ID = "id"
        const val COLUMN_PLANTA_ESPECIE = "especie"
        const val COLUMN_PLANTA_EDAD = "edad"
        const val COLUMN_PLANTA_COLOR = "color"
        const val COLUMN_PLANTA_ALTURA = "altura"
        const val COLUMN_PLANTA_PARCELA_ID = "parcelaId"

        private const val CREATE_TABLE_PLANTA = """
            CREATE TABLE $TABLE_PLANTA (
                $COLUMN_PLANTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLANTA_ESPECIE TEXT NOT NULL,
                $COLUMN_PLANTA_EDAD INTEGER NOT NULL,
                $COLUMN_PLANTA_COLOR TEXT NOT NULL,
                $COLUMN_PLANTA_ALTURA REAL NOT NULL,
                $COLUMN_PLANTA_PARCELA_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_PLANTA_PARCELA_ID) 
                REFERENCES $TABLE_PARCELA($COLUMN_PARCELA_ID) ON DELETE CASCADE
            )
        """

        // Tabla Alarma
        const val TABLE_ALARMA = "Alarma"
        const val COLUMN_ALARMA_ID = "id"
        const val COLUMN_ALARMA_HORA = "hora"
        const val COLUMN_ALARMA_MOTIVO = "motivo"
        const val COLUMN_ALARMA_UBICACION = "ubicacion"

        private const val CREATE_TABLE_ALARMA = """
            CREATE TABLE $TABLE_ALARMA (
                $COLUMN_ALARMA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ALARMA_HORA TEXT NOT NULL,
                $COLUMN_ALARMA_MOTIVO TEXT NOT NULL,
                $COLUMN_ALARMA_UBICACION TEXT NOT NULL
            )
        """
    }


    // Operaciones CRUD para Parcelas
    fun insertarParcela(nombre: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PARCELA_NOMBRE, nombre)
        }
        return db.insert(TABLE_PARCELA, null, values)
    }

    // Operaciones CRUD para Alarmas
    fun insertarAlarma(
        hora: String,
        motivo: String,
        ubicacion: String,
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARMA_HORA, hora)
            put(COLUMN_ALARMA_MOTIVO, motivo)
            put(COLUMN_ALARMA_UBICACION, ubicacion)
        }
        return db.insert(TABLE_ALARMA, null, values)
    }

    fun obtenerAlarmas(): Cursor {
        return readableDatabase.query(
            TABLE_ALARMA,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ALARMA_HORA ASC"
        )
    }

    fun actualizarAlarma(id: String, hora: String, motivo: String, ubicacion: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARMA_HORA, hora)
            put(COLUMN_ALARMA_MOTIVO, motivo)
            put(COLUMN_ALARMA_UBICACION, ubicacion)
        }
        return db.update(
            TABLE_ALARMA,
            values,
            "$COLUMN_ALARMA_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun eliminarAlarma(id: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_ALARMA,
            "$COLUMN_ALARMA_ID=?",
            arrayOf(id.toString())
        )
    }

    // Operaciones CRUD para Plantas
    fun insertarPlanta(
        especie: String,
        edad: Int,
        color: String,
        altura: Double,
        parcelaId: Int
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLANTA_ESPECIE, especie)
            put(COLUMN_PLANTA_EDAD, edad)
            put(COLUMN_PLANTA_COLOR, color)
            put(COLUMN_PLANTA_ALTURA, altura)
            put(COLUMN_PLANTA_PARCELA_ID, parcelaId)
        }
        return db.insert(TABLE_PLANTA, null, values)
    }

    fun obtenerPlantasPorParcela(parcelaId: Int): Cursor {
        return readableDatabase.query(
            TABLE_PLANTA,
            null,
            "$COLUMN_PLANTA_PARCELA_ID = ?",
            arrayOf(parcelaId.toString()),
            null,
            null,
            "$COLUMN_PLANTA_ESPECIE ASC"
        )
    }
}