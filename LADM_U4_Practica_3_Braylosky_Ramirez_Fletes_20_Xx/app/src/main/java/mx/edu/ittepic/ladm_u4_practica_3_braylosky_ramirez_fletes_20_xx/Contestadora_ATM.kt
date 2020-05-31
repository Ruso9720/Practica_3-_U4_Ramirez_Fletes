package mx.edu.ittepic.ladm_u4_practica_3_braylosky_ramirez_fletes_20_xx

class Contestadora_ATM (p:MainActivity) : Thread() {
    var play = false
    var p= p
    override fun run() {
        super.run()
        play = true
        while (play) {
            sleep(2000)
            p.runOnUiThread {
                p.contestar()
            }
        }
    }
}