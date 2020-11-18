package com.dongguk.untactstudy

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.TodoData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_todo.*
import kotlin.collections.ArrayList


class CreateTodoActivity : AppCompatActivity() {

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        // CreateStudyActivity에서 받아온 값
        val studyNumber = intent.getStringExtra("studyNumber")
        val createDate = intent.getStringExtra("studyCreateDate")
        val endDate = intent.getStringExtra("studyEndDate")

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

            // 임시로 주차와 매주 할일의 개수를 강제 세팅함
            var week = 4
            var week_todo : Int = 3

            // 전체 할일의 개수가 주차수와 딱 떨어지지 않는 경우 빈값으로 채움
            // ex) 4주에 총 할일이 10개인 경우, 2개의 빈값을 추가해서 12개로 만듦
            if((todoList.size % week) > 0) {
                for(a in 1 .. (todoList.size % week)) {
                    todoList.add("")
                }
            }

            Log.e(TAG, "데이터 처리할 최종 todoList : "+todoList)


            // 매주 할일에 대한 데이터를 쌓기 위한 변수 선언
            var c = 0
            var d = week_todo

            // 주차별 할일에 대한 데이터를 쌓는다.
            for(i in 1 .. week) {
                var tempList = todoList.subList(c,d)
                Log.e(TAG, "c : "+c+", d : "+d+", tempList"+i+" : "+tempList)

                FirebaseFirestore.getInstance().collection("studyInfo")
                        .document(studyNumber.toString())
                        .collection("todoList")
                        .document(i.toString())
                        .set(TodoData(tempList))
                        .addOnSuccessListener {
                            Log.e(TAG, "todo list 데이터 insert 성공")
                        }

                // 반복을 위한 변수 값 추가
                c = c + week_todo
                d = d + week_todo
            }

        }//button
    }//onCreate
}//Activity