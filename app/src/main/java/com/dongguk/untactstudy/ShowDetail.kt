package com.dongguk.untactstudy

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.Model.StudyModel
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.view.View
import android.widget.Button
import com.dongguk.untactstudy.Model.TodoCountData
import com.dongguk.untactstudy.Model.TodoData
import com.google.firebase.auth.FirebaseAuth


class ShowDetail : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_study_information)

        //getExtra
        val keyValue = intent?.getStringExtra("transfer")

        Log.e(TAG, "key값" + keyValue)

        val tx1 = findViewById<TextView>(R.id.study_name_section)
        val tx2 = findViewById<TextView>(R.id.study_info_section)
        val tx3 = findViewById<TextView>(R.id.study_member_section)
        val tx4 = findViewById<TextView>(R.id.study_real_member_section)
        val tx5 = findViewById<TextView>(R.id.study_start_date_section)
        val tx6 = findViewById<TextView>(R.id.study_end_date_section)
        val tx7 = findViewById<TextView>(R.id.study_sort_1_section)
        val tx8 = findViewById<TextView>(R.id.study_sort_2_section)
        val tx9 = findViewById<TextView>(R.id.study_room_number_section)

        val button = findViewById<Button>(R.id.apply_button_detail)

        button.setOnClickListener {
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //여기다가 가입 기능
                }
            }
        }

        /*
        my_recycler_todo_view.apply{
            my_recycler_todo_view.adapter = totalToDoRecyclerView(keyValue.toString())
            my_recycler_todo_view.layoutManager = LinearLayoutManager(this@ShowDetail)
        }
         */

        FirebaseFirestore.getInstance()
                .collection("studyInfo")
                .document(keyValue.toString())
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val studyData = task.result?.toObject(StudyModel::class.java)
                        tx1.text = studyData?.studyName
                        tx2.text = studyData?.studyInfo
                        tx3.text = studyData?.studyMemberAmount
                        tx4.text = studyData?.realMemberAmount.toString()
                        tx5.text = studyData?.studyStartDate
                        tx6.text = studyData?.studyEndDate
                        tx7.text = studyData?.sort1st
                        tx8.text = studyData?.sort2nd
                        tx9.text = keyValue
                    }
                }

    }

    inner class totalToDoRecyclerView(studyRoomNumber: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {

            TODO("Not yet implemented")
        }

    }
}