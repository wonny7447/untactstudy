package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.StudyModel
import com.dongguk.untactstudy.Model.TodoCountData
import com.dongguk.untactstudy.Model.TodoData
import com.dongguk.untactstudy.navigation.MyStudyFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class ShowDetail : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_study_information)
        setTitle("스터디 지원하기")

        firestore = FirebaseFirestore.getInstance()

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

        val button = findViewById<Button>(R.id.apply_button)

        val studyRoomNum = keyValue.toString()

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

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.e(TAG, "버튼 반응 있음")
                Log.e(TAG, "studyRoomNum : " + studyRoomNum)
                FirebaseFirestore.getInstance()
                        .collection("loginUserData")
                        .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userData = task.result?.toObject(LoginUserData::class.java)
                                var currentStudyRoom = userData?.studyRoomNumber.toString()
                                Log.e(TAG, "currentStudyRoom : " + currentStudyRoom)
                                if (currentStudyRoom == "0") {
                                    Log.e(TAG, "currentStudyRoom 이 0임")
                                    FirebaseFirestore.getInstance()
                                            .collection("loginUserData")
                                            .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                            .update("studyRoomNumber", studyRoomNum)
                                            .addOnSuccessListener {
                                                Log.e(TAG, "loginUserData에 스터디 가입 처리 완료")

                                                FirebaseFirestore.getInstance()
                                                        .collection("studyInfo")
                                                        .document(studyRoomNum)
                                                        .collection("todoList")
                                                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                            if (querySnapshot == null) return@addSnapshotListener
                                                            for (snapshot in querySnapshot?.documents!!) {
                                                                var TodoData = snapshot.toObject(TodoData::class.java)!!.list
                                                                var todoCount: Int = 0

                                                                for (i in 0..((TodoData.size) - 1)) {
                                                                    var orientText: String = TodoData[i]
                                                                    TodoData.set(i, "FF" + orientText)
                                                                    if (orientText != "") {
                                                                        todoCount += 1
                                                                    }
                                                                }

                                                                FirebaseFirestore.getInstance()
                                                                        .collection("loginUserData")
                                                                        .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                        .collection("todoList")
                                                                        .document(snapshot.id)
                                                                        .set(TodoData(TodoData))
                                                                        .addOnSuccessListener {
                                                                            Log.e(TAG, "loginUserData에 todoList 생성 완료")
                                                                        }
                                                                        .addOnFailureListener {
                                                                            Log.e(TAG, "loginUserData에 todoList 생성 실패")
                                                                        }

                                                                FirebaseFirestore.getInstance()
                                                                        .collection("loginUserData")
                                                                        .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                        .collection("todoCount")
                                                                        .document(snapshot.id)
                                                                        .set(TodoCountData(0, todoCount))
                                                                        .addOnSuccessListener {
                                                                            Log.e(TAG, "loginUserData에 todoCount 생성 완료")
                                                                        }
                                                                        .addOnFailureListener {
                                                                            Log.e(TAG, "loginUserData에 todoCount 생성 실패")
                                                                        }
                                                            }
                                                            val builder = AlertDialog.Builder(this@ShowDetail)
                                                            builder.setTitle("지원성공")
                                                            builder.setMessage("정상적으로 가입처리가 완료되었습니다.")
                                                            builder.setPositiveButton("확인"){
                                                                dialog, which ->
                                                                val intent = Intent(this@ShowDetail, MainActivity::class.java)
                                                                startActivity(intent)
                                                            }

                                                            val dialog: AlertDialog = builder.create()
                                                            dialog.show()
                                                        }
                                            }
                                            .addOnFailureListener {
                                                Log.e(TAG, "loginUserData에 스터디 가입 처리 실패")
                                            }
                                } else {
                                    Log.e(TAG, "currentStudyRoom 이 0이 아님")
                                    FirebaseFirestore.getInstance()
                                            .collection("studyInfo")
                                            .document(currentStudyRoom)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                                                    val currentStudyRoomData = task.result?.toObject(StudyModel::class.java)
                                                    var currentStudyRoomEndDate = sdf.parse(currentStudyRoomData?.studyEndDate).time
                                                    var today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                                                    Log.e(TAG, "현재 포함된 스터디 룸의 종료 일자" + currentStudyRoomEndDate + "현재 시간" + today)
                                                    if (currentStudyRoomEndDate < today) {
                                                        Log.e(TAG, "현재 스터디에 포함되어 있지 않음")
                                                        Log.e(TAG, "지원 가능2")

                                                        FirebaseFirestore.getInstance()
                                                                .collection("loginUserData")
                                                                .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                .update("studyRoomNumber", studyRoomNum)
                                                                .addOnSuccessListener {
                                                                    Log.e(TAG, "loginUserData에 스터디 가입 처리 완료")
                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("studyInfo")
                                                                            .document(studyRoomNum)
                                                                            .collection("todoList")
                                                                            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                                                if (querySnapshot == null) return@addSnapshotListener
                                                                                for (snapshot in querySnapshot?.documents!!) {
                                                                                    var TodoData = snapshot.toObject(TodoData::class.java)!!.list

                                                                                    for (i in 0..((TodoData.size) - 1)) {
                                                                                        var orientText: String = TodoData[i]
                                                                                        TodoData.set(i, "FF" + orientText)
                                                                                    }

                                                                                    FirebaseFirestore.getInstance()
                                                                                            .collection("loginUserData")
                                                                                            .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                                            .collection("todoList")
                                                                                            .document(snapshot.id)
                                                                                            .set(TodoData(TodoData))
                                                                                            .addOnSuccessListener {
                                                                                                Log.e(TAG, "loginUserData에 todoList 생성 완료")
                                                                                            }
                                                                                            .addOnFailureListener {
                                                                                                Log.e(TAG, "loginUserData에 todoList 생성 실패")
                                                                                            }
                                                                                }
                                                                            }
                                                                    val builder = AlertDialog.Builder(this@ShowDetail)
                                                                    builder.setTitle("지원성공")
                                                                    builder.setMessage("정상적으로 가입처리가 완료되었습니다.")
                                                                    builder.setPositiveButton("확인"){
                                                                        dialog, which ->
                                                                        val intent = Intent(this@ShowDetail, MainActivity::class.java)
                                                                        startActivity(intent)
                                                                    }

                                                                    val dialog: AlertDialog = builder.create()
                                                                    dialog.show()
                                                                }
                                                                .addOnFailureListener {
                                                                    Log.e(TAG, "loginUserData에 스터디 가입 처리 실패")
                                                                }

                                                    } else {
                                                        val builder = AlertDialog.Builder(this@ShowDetail)
                                                        builder.setTitle("지원불가")
                                                        builder.setMessage("현재 스터디에 가입되어 있어 지원이 불가능합니다.")
                                                        builder.setPositiveButton("확인",null)

                                                        val dialog: AlertDialog = builder.create()

                                                        dialog.show()
                                                    }
                                                }
                                            }
                                }
                            }
                        }
            }
        })

    }

        /*
        my_recycler_todo_view.apply{
            my_recycler_todo_view.adapter = totalToDoRecyclerView(keyValue.toString())
            my_recycler_todo_view.layoutManager = LinearLayoutManager(this@ShowDetail)
        }
         */




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