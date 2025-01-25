package com.example.a3_deber2bimestre

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_PARCELA)
        db?.execSQL(CREATE_TABLE_PLANTA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PARCELA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLANTA")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "ParcelasDB"
        const val DATABASE_VERSION = 1

        // Tabla Parcela
        const val TABLE_PARCELA = "Parcela"
        const val COLUMN_PARCELA_ID = "id"
        const val COLUMN_PARCELA_NOMBRE = "nombre"

        private const val CREATE_TABLE_PARCELA = """
            CREATE TABLE $TABLE_PARCELA (
                $COLUMN_PARCELA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PARCELA_NOMBRE TEXT NOT NULL
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
                FOREIGN KEY($COLUMN_PLANTA_PARCELA_ID) REFERENCES $TABLE_PARCELA($COLUMN_PARCELA_ID)
            )
        """
    }
}
