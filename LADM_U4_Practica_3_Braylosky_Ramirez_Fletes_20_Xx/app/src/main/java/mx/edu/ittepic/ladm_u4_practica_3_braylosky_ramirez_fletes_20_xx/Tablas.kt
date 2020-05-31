package mx.edu.ittepic.ladm_u4_practica_3_braylosky_ramirez_fletes_20_xx

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Tablas(context: Context?, name : String,
             factory: SQLiteDatabase.CursorFactory?,
             version: Int) : SQLiteOpenHelper(context,
              name, factory, version){

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE JUGADORES(ID INTEGER PRIMARY KEY AUTOINCREMENT,NOMBRE VARCHAR(100),POSICION CHAR(3), PRECIO VARCHAR(10), LESIONES VARCHAR(100))")

        db.execSQL("CREATE TABLE SMS(ID INTEGER PRIMARY KEY AUTOINCREMENT, NUMERO VARCHAR(200), CUERPO VARCHAR(200), TIPO CHAR(1))")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}