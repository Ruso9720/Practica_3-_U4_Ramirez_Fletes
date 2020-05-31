package mx.edu.ittepic.ladm_u4_practica_3_braylosky_ramirez_fletes_20_xx

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var nombreBaseDatos = "practica6"
    var atm : Contestadora_ATM?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        atm = Contestadora_ATM(this)
        atm?.start()

        var res = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.RECEIVE_SMS)

        var sms = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.SEND_SMS)

        var rs = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_SMS)


        if( res != PackageManager.PERMISSION_GRANTED || rs != PackageManager.PERMISSION_GRANTED || sms != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_SMS),101)
        }

        comprobarContestacion()

        save.setOnClickListener {
            if(nombre.text.isEmpty() || precio.text.isEmpty() || lesiones.text.isEmpty()){
                AlertDialog.Builder(this).setMessage("EXISTEN CAMPOS VACIOS").show()
                return@setOnClickListener
            }else{
                insertarJugador()
                limpiar()
            }
        }
    }



    private fun insertarJugador() {
        try {
            var baseDatos = Tablas(this,nombreBaseDatos,null,1)
            var insertar = baseDatos.writableDatabase
            var SQL = "INSERT INTO JUGADORES VALUES(NULL,'${nombre.text.toString().toUpperCase()}'," +
                                                               "'${pos.text.toString().toUpperCase()}'," +
                                                               "'${precio.text.toString()}'," +
                                                                "'${lesiones.text.toString()}')"
            insertar.execSQL(SQL)
            baseDatos.close()
        }catch (e: SQLiteException){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
        AlertDialog.Builder(this).setMessage("SE RESGISTRO CORRECTAMENTE AL JUGADOR").show()
    }
    fun contestar() {
        var posicion = ""
        var nombre = ""
        var sms2 =""

        try {
            var cursor = Tablas(this,nombreBaseDatos,null,1).readableDatabase
                .rawQuery("SELECT * FROM SMS",null)

            var telefono = ""
            var msg3 = ""
            var tipo = ""

            if(cursor.moveToFirst()) {
                do {
                    telefono = cursor.getString(1)
                    msg3 = cursor.getString(2)
                    tipo = cursor.getString(3)

                    if (smsCorrecto(msg3)) {
                        var cadena = msg3.split("-")
                        nombre = cadena[1]
                        posicion = cadena[2]


                        sms2 = jugadorDisponible(nombre,posicion)

                        if (tipo.equals("X")) {
                            SmsManager.getDefault().sendTextMessage(telefono, null, sms2, null, null)
                            contestadoNoContestado(telefono)
                        }
                    } else {
                        if (tipo.equals("X")) {
                            sms2 = "FORMA INCORRECTA DE PEDIR INFORACION \n\n ESCRIBIR DE LA SIGUIENTE MANERA: \n" +
                                    "PRECIO-ALEXISVEGA-DEL-LESION \n TODO EN MAYUSCULAS"
                            SmsManager.getDefault().sendTextMessage(telefono, null, sms2, null, null)
                            contestadoNoContestado(telefono)
                        }
                    }
                }while(cursor.moveToNext())
            }
            cursor.close()
        }catch (e: SQLiteException){
            AlertDialog.Builder(this).setMessage(e.message).show()
        }
    }
    fun contestadoNoContestado (telefono:String) {

        try {
            var baseDatos = Tablas(this, nombreBaseDatos, null, 1)
            var insertar = baseDatos.writableDatabase
            var SQL = "UPDATE SMS SET TIPO ='A' WHERE NUMERO = ?"
            var consulta = arrayOf(telefono)
            insertar.execSQL(SQL, consulta)

            insertar.close()
            baseDatos.close()

        }catch (e: SQLiteException) {
            AlertDialog.Builder(this).setMessage(e.message).show()
        }
    }
fun comprobarContestacion() {
    try{
        var mensaje = ""
        val puntero = Tablas(this,
            nombreBaseDatos, null, 1).readableDatabase.rawQuery("SELECT * FROM SMS", null)

        if(puntero.moveToFirst()){
            var tipo = ""
            if(puntero.getString(3).equals("A")){
                tipo = "APLICADO"
            }else{
                tipo = "NO APLICADO"
            }
            do{
                mensaje = "TELEFONO CONTESTADO: " + puntero.getString(1)+"\nMENSAJE: "+ puntero.getString(2)+"\nTIPO: "+ tipo
            }while(puntero.moveToNext())
        }else{
            mensaje = "LA CONTESTADORA ATM NO HA SIDO UTILIZADA"
        }
        textView2.setText(mensaje)
    }catch (e:SQLiteException){
        AlertDialog.Builder(this).setMessage(e.message).show()
    }
}
    private fun limpiar() {
        nombre.setText("")
        pos.setText("")
        precio.setText("")
        lesiones.setText("")
    }

fun smsCorrecto(mensaje:String):Boolean{
    try{

        var cadena = mensaje.split("-")
        var posicion = cadena[2].length
        var dinero = cadena[0]
        var lesion = cadena[3]



        if(lesion.equals("LESION") && dinero.equals("PRECIO") && posicion == 3){
            return true
        }
    }catch (e:IndexOutOfBoundsException){
        return false
    }
    return false
}


fun jugadorDisponible(nombre:String,posicion:String):String{
    var consulta = ""
    try {
        var baseDatos = Tablas(this,nombreBaseDatos,null,1)
        var select = baseDatos.readableDatabase
        var SQL = "SELECT NOMBRE,POSICION,PRECIO,LESIONES FROM JUGADORES WHERE NOMBRE = ? AND POSICION = ?"
        var condicion = arrayOf(nombre,posicion)
        var cursor = select.rawQuery(SQL,condicion)

        if(cursor.moveToFirst()){
            consulta = "NOMBRE: " + cursor.getString(0) +"\n  POSICION: "+cursor.getString(1)+
                    "\n PRECIO: " + cursor.getString(2) +" MILLONES (DOLARES)"+"\n  LESIONES: "+cursor.getString(3)
        }
        else{

            consulta = "NO HAY JUGADORES EN LA PLANTILLA PARA LA TEMPORADA 2019 - 2020 QUE COINCIDAN CON LOS CRITERIOS DE BUSQUEDA " +
                    "O LA POSICION DEL JUGADOR E INCORRECTA"
        }
        select.close()
        baseDatos.close()

    }catch (e:SQLiteException){
        AlertDialog.Builder(this).setMessage(e.message).show()
    }

    return consulta
}







    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 101){
            comprobarContestacion()
        }
    }

}
