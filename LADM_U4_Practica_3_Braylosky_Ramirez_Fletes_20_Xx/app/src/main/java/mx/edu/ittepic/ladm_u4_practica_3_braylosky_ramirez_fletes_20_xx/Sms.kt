package mx.edu.ittepic.ladm_u4_practica_3_braylosky_ramirez_fletes_20_xx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class Sms : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        var nombreBaseDatos = "practica6"
        var extras = intent.extras

        if(extras != null){

            var sp = extras.get("pdus") as Array<Any>

            for(indice in sp.indices){

                var formato = extras.getString("format")
                var sms  = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    SmsMessage.createFromPdu(sp[indice] as ByteArray, formato)

                }else{

                    SmsMessage.createFromPdu(sp[indice] as ByteArray)
                }


                var cuerpo_mensaje = sms.messageBody.toString()
                var numero_tel = sms.originatingAddress



                try{
                    var baseDatos = Tablas(context, nombreBaseDatos, null, 1)
                    var insert = baseDatos.writableDatabase
                    var SQL = "INSERT INTO SMS VALUES(NULL,'${numero_tel}','${cuerpo_mensaje}', 'X')"
                    insert.execSQL(SQL)
                    baseDatos.close()
                }catch (e: SQLiteException){
                    AlertDialog.Builder(context).setMessage(e.message).show()
                }
                AlertDialog.Builder(context).setMessage("SE RESGISTRO CORRECTAMENTE EL ENVIO DE MENSAJE").show()
            }
        }
    }
}



