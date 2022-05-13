package mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.databinding.ActivitySearchScreenBinding

class SearchScreen : AppCompatActivity() {
    lateinit var binding: ActivitySearchScreenBinding
    private val fireData = FirebaseFirestore.getInstance()
    private val areaCollRef = fireData.collection("area")
    private val arrayRecADiv = ArrayList<String>()
    private val arrayRecADes = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "Ventan de busqueda"

        binding.btnSSback.setOnClickListener {
            finish()
        }

        binding.btnSSsearch.setOnClickListener {
            if (!binding.rbtnsubdepa.isChecked && !binding.rbtnArea.isChecked) {
                mensaje("No has seleccionado en donde buscar....")
            }

            if (binding.rbtnsubdepa.isChecked) {
                val ventanSubDepa = Intent(this, SearchSubDepa::class.java)
                ventanSubDepa.putExtra("edificio", binding.txtIdEdificioSD.text.toString())
                ventanSubDepa.putExtra("descripcion", binding.txtSSdescripcionSub.text.toString())
                ventanSubDepa.putExtra("division", binding.txtSSdivisionSub.text.toString())
                startActivity(ventanSubDepa)
            }
            if (binding.rbtnArea.isChecked) {
                popUpArea(
                    binding.txtSSdivisionArea.text.toString(),
                    binding.txtSSdescripcionArea.text.toString()
                )
            }
        }

    }// fin del oncreate

/*                        Inicio de los metodos para mensajes                          */

    private fun popUpArea(divi: String, desc: String) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogAreaSearch = inflater.inflate(R.layout.search_area_popup, null)
        val lvAreaDiv = dialogAreaSearch.findViewById<ListView>(R.id.lvAreaSearchDiv)
        arrayRecADiv.clear()
        arrayRecADes.clear()

        areaCollRef.whereEqualTo("division", divi).get().addOnSuccessListener {
            for (document in it) {
                val documento = "Division: ${document.getString("division")}"
                arrayRecADiv.add(documento)
            }
            lvAreaDiv.adapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayRecADiv)
        }

        areaCollRef.whereEqualTo("descripcion", desc).get().addOnSuccessListener {
            for (document in it) {
                val documento = "Descripción: ${document.getString("descripcion")}"
                arrayRecADes.add(documento)
            }
            lvAreaDiv.adapter =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayRecADes)
        }

        with(builder) {
            setTitle("Área")
                .setPositiveButton("Ok") { _, _ -> }
                .setView(dialogAreaSearch)
                .show()
        }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

}// fin del SearchScreen