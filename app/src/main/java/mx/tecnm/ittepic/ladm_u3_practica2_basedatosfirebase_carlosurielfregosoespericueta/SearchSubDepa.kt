package mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.databinding.ActivitySearchSubDepaBinding

class SearchSubDepa : AppCompatActivity() {

    private lateinit var binding: ActivitySearchSubDepaBinding
    private val fireData = FirebaseFirestore.getInstance()
    private val areaCollRef = fireData.collection("area")
    private val arrayRecEd = ArrayList<String>()
    private val arrayRecDiv = ArrayList<String>()
    private val arrayRecDes = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        this.title = "Busqueda en SubDepartamento"
        binding = ActivitySearchSubDepaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intentEdificio = this.intent.extras!!.getString("edificio")
        val intentDescripcion = this.intent.extras!!.getString("descripcion")
        val intentDivision = this.intent.extras!!.getString("division")

        arrayRecEd.clear()
        fireData.collection("area").whereEqualTo("subDepa.idEdificio", intentEdificio)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val documento =
                        "Área: ${document.getString("division")}\nEdificio: ${document.getString("subDepa.idEdificio")}"
                    arrayRecEd.add(documento)
                }
                binding.lvSearchSubDepaPopEd.adapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayRecEd)
            }// success listener del edificio
            .addOnFailureListener {
                mensaje("Error...\n${it.message}")
            }// fin del failure y del edificio

        // divison
        arrayRecDiv.clear()

        areaCollRef.whereEqualTo("division", intentDivision)
            .get().addOnSuccessListener {
                for (document in it) {
                    val documento =
                        "División: ${document.getString("division")}\nPiso: ${document.getString("subDepa.piso")}"
                    arrayRecDiv.add(documento)
                    binding.lvSearchSubDepaPopDiv.adapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayRecDiv)
                }
            }// success listener de la division
            .addOnFailureListener {
                mensaje("Error...\n${it.message}")
            } // fin del failure y de la division

        // descripcion
        arrayRecDes.clear()


        areaCollRef.whereEqualTo("descripcion", intentDescripcion)
            .get().addOnSuccessListener {
                for (document in it) {
                    val documento =
                        "Descripción: ${document.getString("descripcion")}\nEmpleados: ${document.getLong("cantEmpleados")}"
                    arrayRecDes.add(documento)
                    binding.lvSearchSubDepaPopDes.adapter =
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayRecDes)
                }
            }// success listener de la descripcion
            .addOnFailureListener {
                mensaje("Error...\n${it.message}")
            }// fin del failure y de la descripcion


        binding.btnSalir.setOnClickListener { finish() }

    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

}