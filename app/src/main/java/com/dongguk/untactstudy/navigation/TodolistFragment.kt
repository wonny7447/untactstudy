package com.dongguk.untactstudy.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.GetStudyModel
import com.dongguk.untactstudy.StudyModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_todolist.*
import kotlinx.android.synthetic.main.fragment_todolist.view.*

class TodolistFragment : Fragment(){

    var studyIndex = mutableListOf<String>("0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0")



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_todolist, container, false)

        fun Show() {

            var studyWeek = 1

            if (studyWeek > 1) {
                view.todolistPrevWeek.text = studyIndex[studyWeek - 2]
            } else {
                view.todolistPrevWeek.text = "첫 주입니다."
            }
            view.todolistThisWeek.text = studyIndex[studyWeek - 1]
            Log.d("1", studyIndex.toString())
            if (studyWeek < 16) {
                view.todolistNextWeek.text = studyIndex[studyWeek]
            } else {
                view.todolistPrevWeek.text = "마지막 주입니다."
            }

        }

        var studyIndexList = ArrayList<GetStudyModel>()
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("studyInfo").document("1")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("1", "DocumentSnapshot data: ${document.data}")
                    studyIndex[0] = document.data?.get("studyIndex1").toString()
                    studyIndex[1] = document.data?.get("studyIndex2").toString()
                    studyIndex[2] = document.data?.get("studyIndex3").toString()
                    studyIndex[3] = document.data?.get("studyIndex4").toString()
                    studyIndex[4] = document.data?.get("studyIndex5").toString()
                    studyIndex[5] = document.data?.get("studyIndex6").toString()
                    studyIndex[6] = document.data?.get("studyIndex7").toString()
                    studyIndex[7] = document.data?.get("studyIndex8").toString()
                    studyIndex[8] = document.data?.get("studyIndex9").toString()
                    studyIndex[9] = document.data?.get("studyIndex10").toString()
                    studyIndex[10] = document.data?.get("studyIndex11").toString()
                    studyIndex[11] = document.data?.get("studyIndex12").toString()
                    studyIndex[12] = document.data?.get("studyIndex13").toString()
                    studyIndex[13] = document.data?.get("studyIndex14").toString()
                    studyIndex[14] = document.data?.get("studyIndex15").toString()
                    studyIndex[15] = document.data?.get("studyIndex16").toString()
                    Log.d("1", "studyIndex : $studyIndex")
                    Show()
                } else {
                    Log.d("1", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("1", "get failed with ", exception)
            }



        return view
    }
}
