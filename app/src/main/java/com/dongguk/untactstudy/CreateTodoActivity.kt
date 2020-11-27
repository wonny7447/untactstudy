package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.StudyTodoData
import com.dongguk.untactstudy.Model.TodoData
import com.dongguk.untactstudy.navigation.MyStudyFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_todo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreateTodoActivity : AppCompatActivity() {

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        // CreateStudyActivity에서 받아온 값
        val studyNumber = intent.getStringExtra("studyNumber")
        val startDate = intent.getStringExtra("studyStartDate")
        val endDate = intent.getStringExtra("studyEndDate")

        Log.e(TAG, "startDate : "+startDate+", endDate : "+endDate)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val studyStartDate : Long = sdf.parse(startDate).time
        val studyEndDate : Long = sdf.parse(endDate).time
        var diff : Long = studyStartDate - studyEndDate
        diff /= (24 * 60 * 60 * 1000)
        diff = Math.abs(diff)

        Log.e(TAG, "studyCreateDate : "+studyStartDate+", studyEndDate : "+studyEndDate+", diff : "+diff)

        //to do 리스트를 만들고, 안드로이드에서 제공하는 기본 레이아웃에 adapter로 연결
        var todoList = ArrayList<String>()
        var todoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todoList)

        todoListView.adapter = todoAdapter
        todoListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // 리스트뷰의 아이템 클릭 시 발생하는 이벤트
        todoListView.setOnItemClickListener { parent, view, position, id ->
            Log.e(TAG, "value : "+ todoList.get(position)+", position : "+ position)
        }

        // 추가 버튼 클릭 시 발생하는 이벤트
        addButton.setOnClickListener {
            if(todoInput != null && todoInput.text.isNotEmpty()) {
                todoList.add(todoInput.text.toString())
                todoAdapter.notifyDataSetChanged()
                todoInput.text = null
                todoInput.hint = "목차를 입력하세요."
            }
        }

        // 완료 버튼 클릭 시 발생하는 이벤트
        createTodoButton.setOnClickListener {

            // 스터디 생성일
            var week : Long = 0
            if((diff % 7) > 0) {
                week = (diff / 7) + 1
            } else {
                week = diff / 7
            }

            var week_todo : Long = 0
            if((todoList.size % week) > 0) {
                week_todo = (todoList.size / week) + 1
            } else {
                week_todo = (todoList.size / week)
            }

            // 전체 할일의 개수가 주차수와 딱 떨어지지 않는 경우 빈값으로 채움
            // ex) 4주에 총 할일이 10개인 경우, 2개의 빈값을 추가해서 12개로 만듦
            if((todoList.size % week) > 0) {
                for(a in 1 .. (week - (todoList.size % week))) {
                    todoList.add("")
                }
            }

            Log.e(TAG, "데이터 처리할 최종 todoList : "+todoList)


            // 매주 할일에 대한 데이터를 쌓기 위한 변수 선언
            var c : Long = 0
            var d : Long = week_todo

            // 주차별 할일에 대한 데이터를 쌓는다.
            for(i in 1 .. week) {
                var tempList = todoList.subList(c.toInt(), d.toInt())
                Log.e(TAG, "c : "+c+", d : "+d+", tempList"+i+" : "+tempList)
                var countList = List<Int>(tempList.size) {0}

                FirebaseFirestore.getInstance().collection("studyInfo")
                    .document(studyNumber.toString())
                    .collection("todoList")
                    .document(i.toString())
                    .set(StudyTodoData(countList as MutableList<Int>, tempList))
                    .addOnSuccessListener {
                        Log.e(TAG, "week : "+i+", studyInfo에 todo list 데이터 insert 성공")

                        for(i in 0 .. ((tempList.size) - 1)) {
                            var orientText : String = tempList[i]
                            tempList[i] = "FF"+orientText
                        }

                        FirebaseFirestore.getInstance().collection("loginUserData")
                            .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                            .collection("todoList")
                            .document(i.toString())
                            .set(TodoData(tempList))
                            .addOnSuccessListener {
                                Log.e(TAG, "week : "+i+", loginUserData에 todo list 데이터 insert 성공")
                            }
                    }

                // 반복을 위한 변수 값 추가
                c = c + week_todo
                d = d + week_todo
            }

            startActivity(Intent(this, MainActivity::class.java))
        }//button
    }//onCreate
}//Activity