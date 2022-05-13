package mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.SearchScreen
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.SearchSubDepa
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    /*-------------------------------- Variables Globales ----------------------------------------*/
    private var fireData = FirebaseFirestore.getInstance()
    private var depaId = 1
    private val collectionAreaRef = fireData.collection("area")


    /*--------------------------------------------------------------------------------------------*/
    /*------------------------------------- Useful things section --------------------------------*/
    /*
    * retrive last document https://stackoverflow.com/questions/52362292/how-to-retrieve-the-last-document-in-a-firebase-collection-i-would-also-like-to
    * insert array in firestore https://stackoverflow.com/questions/52813901/how-to-insert-array-of-objects-in-firestore
    * insert object data safetly https://stackoverflow.com/questions/50592980/add-new-field-in-nested-object-firestore-android
    * nested object in firestore https://www.youtube.com/watch?v=errtXHEvzsc (issue cause have 3-4 years old)
    * */
    /*--------------------------------------------------------------------------------------------*/
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*--------------------------- Editar código desde aquí -----------------------------*/
        // definiendo la funcionalidad de los radio buttons
        val txtEdificio = binding.txtHIdEdificio.text
        val txtDescripcion = binding.txtHdescripcion.text
        val txtCantidad = binding.txtHcantEmpleados.text
        val txtDivison = binding.txtHdivision.text
        val txtPiso = binding.txtHpiso.text

        binding.btnHinsertar.setOnClickListener {
            if(txtEdificio.isBlank() || txtPiso.isBlank() || txtDescripcion.isBlank() || txtDivison.isBlank() || txtCantidad.isBlank()) {
                mensaje("No puedes dejar todos los campos vacios....\nLlena uno")
                return@setOnClickListener
            } else {
                insertar()
                binding.txtHdescripcion.setText("")
                binding.txtHcantEmpleados.setText("")
                binding.txtHpiso.setText("")
                binding.txtHIdEdificio.setText("")
                binding.txtHdivision.setText("")
                binding.txtHdescripcion.requestFocus()
            }
        }

        binding.btnHbusqueda.setOnClickListener {
            val searchWindow = Intent(context, SearchScreen::class.java)
            startActivity(searchWindow)
        }

        /*--------------------------------- Hasta aquí -------------------------------------*/
        return root
    }

    private fun alerta(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(requireContext()).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){ _, _ -> }
            .show()
    }

    private fun insertar(){
        val areaSubDepa = hashMapOf(
            "descripcion" to binding.txtHdescripcion.text.toString(),
            "division" to binding.txtHdivision.text.toString(),
            "cantEmpleados" to binding.txtHcantEmpleados.text.toString().toInt(),
            "subDepa" to hashMapOf(
                "idEdificio" to binding.txtHIdEdificio.text.toString(),
                "piso" to binding.txtHpiso.text.toString(),
                "idSubdepa" to depaId.toString()
            )//Hash map del subdepa
        )// hash map del area

        collectionAreaRef.add(areaSubDepa).addOnSuccessListener {
            alerta("Área y subdepartamento agregados correctamente")
        }.addOnFailureListener {
            mensaje("No se pudo insertar \n ${it.message}")
        }
        depaId++
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}