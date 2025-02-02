package com.example.examen2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "LocationsDB"
        private const val DATABASE_VERSION = 1

        // Tabla de Ubicaciones
        private const val TABLE_LOCATIONS = "locations"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"

        // Tabla de Detalles (relación uno a muchos con locations)
        private const val TABLE_DETAILS = "location_details"
        private const val COLUMN_DETAIL_ID = "detail_id"
        private const val COLUMN_LOCATION_ID = "location_id"
        private const val COLUMN_DETAIL_TITLE = "title"
        private const val COLUMN_DETAIL_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createLocationsTable = """
            CREATE TABLE $TABLE_LOCATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_LATITUDE REAL NOT NULL,
                $COLUMN_LONGITUDE REAL NOT NULL
            )
        """.trimIndent()

        val createDetailsTable = """
            CREATE TABLE $TABLE_DETAILS (
                $COLUMN_DETAIL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOCATION_ID INTEGER,
                $COLUMN_DETAIL_TITLE TEXT NOT NULL,
                $COLUMN_DETAIL_DESCRIPTION TEXT,
                FOREIGN KEY($COLUMN_LOCATION_ID) REFERENCES $TABLE_LOCATIONS($COLUMN_ID)
            )
        """.trimIndent()

        db.execSQL(createLocationsTable)
        db.execSQL(createDetailsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DETAILS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    // CRUD Operations para Ubicaciones
    fun insertLocation(location: Location): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, location.name)
            put(COLUMN_DESCRIPTION, location.description)
            put(COLUMN_LATITUDE, location.latitude)
            put(COLUMN_LONGITUDE, location.longitude)
        }
        return db.insert(TABLE_LOCATIONS, null, values)
    }

    fun getAllLocations(): ArrayList<Location> {
        val locations = ArrayList<Location>()
        val selectQuery = "SELECT * FROM $TABLE_LOCATIONS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val location = Location(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        latitude = it.getDouble(it.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        longitude = it.getDouble(it.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                    )
                    locations.add(location)
                } while (it.moveToNext())
            }
        }
        return locations
    }

    fun updateLocation(location: Location): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, location.name)
            put(COLUMN_DESCRIPTION, location.description)
            put(COLUMN_LATITUDE, location.latitude)
            put(COLUMN_LONGITUDE, location.longitude)
        }
        return db.update(TABLE_LOCATIONS, values, "$COLUMN_ID = ?", arrayOf(location.id.toString()))
    }

    fun deleteLocation(locationId: Int): Int {
        val db = this.writableDatabase
        // Primero eliminar los detalles asociados
        db.delete(TABLE_DETAILS, "$COLUMN_LOCATION_ID = ?", arrayOf(locationId.toString()))
        // Luego eliminar la ubicación
        return db.delete(TABLE_LOCATIONS, "$COLUMN_ID = ?", arrayOf(locationId.toString()))
    }

    // CRUD Operations para Detalles
    fun insertDetail(detail: LocationDetail): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOCATION_ID, detail.locationId)
            put(COLUMN_DETAIL_TITLE, detail.title)
            put(COLUMN_DETAIL_DESCRIPTION, detail.description)
        }
        return db.insert(TABLE_DETAILS, null, values)
    }

    fun getDetailsForLocation(locationId: Int): ArrayList<LocationDetail> {
        val details = ArrayList<LocationDetail>()
        val selectQuery = "SELECT * FROM $TABLE_DETAILS WHERE $COLUMN_LOCATION_ID = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(locationId.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val detail = LocationDetail(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_DETAIL_ID)),
                        locationId = it.getInt(it.getColumnIndexOrThrow(COLUMN_LOCATION_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_DETAIL_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(COLUMN_DETAIL_DESCRIPTION))
                    )
                    details.add(detail)
                } while (it.moveToNext())
            }
        }
        return details
    }
}