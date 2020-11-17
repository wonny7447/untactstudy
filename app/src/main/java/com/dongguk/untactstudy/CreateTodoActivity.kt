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

            FirebaseFirestore.getInstance().collection("studyInfo")
                    .document(studyNumber.toString())
                    .collection("todoList")
                    .add(TodoData(todoList))
                    .addOnSuccessListener {
                        Log.e(TAG, "todo list 데이터 insert 성공")
                    }

        }//button
    }//onCreate
}//Activity