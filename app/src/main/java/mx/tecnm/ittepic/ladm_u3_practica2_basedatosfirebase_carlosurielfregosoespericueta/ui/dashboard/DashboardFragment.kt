package mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.R
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.databinding.FragmentDashboardBinding
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.ui.home.HomeFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /*---------------------------------------- Variables globales -----------------------------------------*/
    var fireData = FirebaseFirestore.getInstance()
    private val collectionAreaRef = fireData.collection("area")
    private val dataListArea = ArrayList<String>()
    private val dataListSub = ArrayList<String>()
    private val listaIds = ArrayList<String>()

    /*------------------------------------------------------------------------------------------------------*/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        /*------------------------------------------ Incio seguro de código -------------------------------------*/


        /*---------------------------------------- Fin espacio seguro de código ---------------------------------*/
        return binding.root
    }

    /*------------------------------------------ Incio Métodos de la base -------------------------------------*/
    private fun mostrar() {
        dataListArea.clear()
        listaIds.clear()
        dataListSub.clear()
        fireData.collection("area").addSnapshotListener { querySnapshot, ffException ->
            if (ffException != null) {
                alerta(ffException.toString())
                return@addSnapshotListener
            }
            for (document in querySnapshot!!) {
                //dataArrayMap = document.get("subDepa") as ArrayList<String>
                val division ="El area de ${document.getString("division")}\ncuenta con ${document.getLong("cantEmpleados")} empleados"
                val subdepa =
                    "${document.getString("division")} esta en: ${document.getString("subDepa.idEdificio")}\nen: ${document.getString("subDepa.piso")}"
                dataListArea.add(division)
                dataListSub.add(subdepa)
                listaIds.add(document.id)
            }// fin del for

            binding.lvDarea.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataListArea)
            binding.lvDsubDepa.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,dataListSub)

            binding.lvDarea.setOnItemClickListener { _, _, i, _ ->
                val ide = listaIds[i]
                AlertDialog.Builder(requireContext())
                    .setMessage("¿Que deseas hacer con ${dataListArea[i]}?")
                    .setPositiveButton("Eliminar") { _, _ -> eliminar(ide) }
                    .setNegativeButton("Modificar") { _, _ -> actualizar(ide); mostrar() }
                    .setNeutralButton("Cerrar") { _, _ -> }
                    .show()
            }
            binding.lvDsubDepa.setOnItemClickListener { _, _, i, _ ->
                val ide = listaIds[i]
                AlertDialog.Builder(requireContext())
                    .setMessage("¿Que deseas hacer con ${dataListSub[i]}?")
                    .setPositiveButton("Eliminar") { _, _ -> eliminar(ide) }
                    .setNegativeButton("Modificar") { _, _ -> actualizar(ide); mostrar() }
                    .setNeutralButton("Cerrar") { _, _ -> }
                    .show()
            }


        }// fin del snapshot listener
    }// fin del método para mostrar


    /*----------------------------------------- Fin Métodos de la base -------------------------------------*/


    private fun eliminar(idSeleccionado: String) {
        fireData.collection("area").document(idSeleccionado).delete()
            .addOnFailureListener {
                alerta("No se pudo eliminar....\n${it.message!!}")
            }
            .addOnSuccessListener {
                alerta("Se ha eliminado con exito")
            }
        mostrar()
    }

    private fun actualizar(IdElegido: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogUpdate = inflater.inflate(R.layout.udpate_screen, null)
        val txtDescripcionArea = dialogUpdate.findViewById<EditText>(R.id.txtUSdescripcion)
        val txtDivisionArea = dialogUpdate.findViewById<EditText>(R.id.txtUSdivision)
        val txtCantEmpleadosArea = dialogUpdate.findViewById<EditText>(R.id.txtUScantEmpleados)
        val txtIdEdificioSub = dialogUpdate.findViewById<EditText>(R.id.txtUSIdEdificio)
        val txtPisoSub = dialogUpdate.findViewById<EditText>(R.id.txtUSpiso)
        val docRef = collectionAreaRef.document(IdElegido)

        getDocument(docRef,txtDescripcionArea,txtDivisionArea,txtCantEmpleadosArea,txtIdEdificioSub,txtPisoSub)

        with(builder) {
            setTitle("Actualizaciones")
            setPositiveButton("Ok") { _, _ ->
                collectionAreaRef.document(IdElegido).update("descripcion",txtDescripcionArea.text.toString())
                    .addOnFailureListener {
                        mensaje("Error... \n${it.message}")
                    }
                // fin del id del edificio

                collectionAreaRef.document(IdElegido).update("division",txtDivisionArea.text.toString())
                    .addOnFailureListener {
                        mensaje("Error... \n${it.message}")
                    }
                // fin del id del edificio

                collectionAreaRef.document(IdElegido).update("cantEmpleados",txtCantEmpleadosArea.text.toString().toInt())
                    .addOnFailureListener {
                        mensaje("Error... \n${it.message}")
                    }
                // fin del id del edificio

                collectionAreaRef.document(IdElegido).update("subDepa.idEdificio", txtIdEdificioSub.text.toString())
                    .addOnFailureListener {
                        mensaje("Error... \n${it.message}")
                    }
                // fin del id del edificio

                collectionAreaRef.document(IdElegido).update("subDepa.piso", txtPisoSub.text.toString())
                    .addOnSuccessListener {
                        alerta("Campo actualizado de manera exitosa")
                    }
                    .addOnFailureListener {
                        mensaje("Error... \n${it.message}")
                    }
                // fin del id del edificio
            }// fin del possitive
            setNeutralButton("cerrar"){_,_ ->}
                .setView(dialogUpdate)
                .show()
        }// fin del builder para el alert dialog
    }// fin del metodo para elegir

    private fun getDocument(ref: DocumentReference,descET: EditText,divET: EditText,empleadoET: EditText,edificioET: EditText,pisoET: EditText) {
        ref.get().addOnSuccessListener {
            if(it.exists()){
                val descrip = it.getString("descripcion")
                val divison = it.getString("division")
                val empleados = it.getLong("cantEmpleados")
                val edificio = it.getString("subDepa.idEdificio")
                val piso = it.getString("subDepa.piso")
                descET.setText(descrip)
                divET.setText(divison)
                empleadoET.setText(empleados.toString())
                edificioET.setText(edificio)
                pisoET.setText(piso)
            }else{
                alerta("Error de documento, no existe....")
            }
        }.addOnFailureListener {
            mensaje("Error \n${it.message}")
        }

    }

    /*------------------------------------ Métodos de mensajes ----------------------------------*/
    private fun alerta(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(requireContext()).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK") { d, i -> }
            .show()
    }

    override fun onResume() {
        mostrar()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}