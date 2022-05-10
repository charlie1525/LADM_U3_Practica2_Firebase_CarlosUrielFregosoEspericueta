package mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.ittepic.ladm_u3_practica2_basedatosfirebase_carlosurielfregosoespericueta.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    /*-------------------------------- Variables Globales ----------------------------------------*/
    var fireData = FirebaseFirestore.getInstance()
    val dataList = ArrayList<String>()
    val listaIds = ArrayList<String>()
    var depaId = 2
    var hashMap = ArrayList<String>()
    val colAreaRef = fireData.collection("area")
    val colSubDRef = fireData.collection("subdepa")

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



        /*--------------------------------- Hasta aquí -------------------------------------*/
        return root
    }

    private fun alerta(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(requireContext()).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){ d,i-> }
            .show()
    }

    private fun eliminar(idSeleccionado: String) {
        fireData.collection("area").document(idSeleccionado).delete()
            .addOnFailureListener {
                alerta("No se pudo eliminar....\n${it.message!!}")
            }
            .addOnSuccessListener {
                alerta("Se haa eliminado con exito")
            }
        mostrar()
    }

    private fun actualizarEdificio(IdElegido: String){

    }

    fun mostrar(){
        dataList.clear()
        listaIds.clear()
        fireData.collection("area").addSnapshotListener { querySnapshot, ffException ->
            if (ffException != null) {
                alerta(ffException.toString())
                return@addSnapshotListener
            }
            for (document in querySnapshot!!) {
                hashMap = document.get("subDepa") as ArrayList<String>
                val division =
                    "${document.getString("Division")}\nEn : ${hashMap[1]}"
                dataList.add(division)
                listaIds.add(document.id)
            }// fin del for

            binding.lvHareas.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,dataList)
            binding.lvHareas.setOnItemClickListener { adapterView, view, i, l ->
                val ide =  listaIds[i]
                AlertDialog.Builder(requireContext()).setMessage("¿Que deseas hacer con ${dataList[i]}?")
                    .setPositiveButton("Eliminar"){_,_ -> eliminar(ide) }
                    .setNegativeButton("Modificar"){_,_ -> actualizarEdificio(ide) }
                    .setNeutralButton("Cerrar"){_,_ -> }
                    .show()
            }
            // insert array in firestore https://stackoverflow.com/questions/52813901/how-to-insert-array-of-objects-in-firestore

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        mostrar()
        super.onResume()
    }
}