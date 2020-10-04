package kr.ac.gachon.searchdogs.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_dict.*
import kr.ac.gachon.searchdogs.R
import kr.ac.gachon.searchdogs.adapter.DictAdapter
import kr.ac.gachon.searchdogs.data.DogDTO

/**
 * A simple [Fragment] subclass.
 */
class DictFragment : Fragment() {
    var db = FirebaseFirestore.getInstance()
    var dogList = arrayListOf<DogDTO>()
    private var Dprogress : ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Dprogress = view?.findViewById(R.id.DictProgressbar)
        return inflater.inflate(R.layout.fragment_dict, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        val thread = Thread(Runnable {
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(activity, "사전 불러오는 중", Toast.LENGTH_SHORT).show()
                view.findViewById<ProgressBar>(R.id.DictProgressbar).visibility = View.VISIBLE
            }

            db.collection("DogBreeds").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val dogdto = document.toObject(DogDTO::class.java)
                        dogList.add(dogdto)
                    }
                    /*
                    MainActivity 에 아직 attach 안된 오류를 방지하기 위해 작성
                    출처: https://stackoverflow.com/questions/45739067/firebase-database-illegalstateexception-fragment-not-attached-to-activity
                    19.11.04 by 류일웅
                     */
                    val activity = activity
                    if (activity != null && isAdded) {
                        val dogAdapter = DictAdapter(this.requireContext(), dogList)
                        listView_Dict.adapter = dogAdapter
                        view.findViewById<ProgressBar>(R.id.DictProgressbar).visibility = View.INVISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        })
        thread.start()
    }
}
