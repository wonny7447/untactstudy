package com.dongguk.untactstudy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.QuizModel
import com.dongguk.untactstudy.Model.ScoreModel
import com.dongguk.untactstudy.navigation.TodolistFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.fragment_todolist.*
import kotlinx.android.synthetic.main.quiz_list_row.*
import kotlinx.android.synthetic.main.quiz_list_row.view.*

class QuizActivity : AppCompatActivity() {

    //로그 변수
    private val TAG = LoginActivity::class.java.simpleName

    val correctAnswerList = booleanArrayOf(false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        var studyRoomNumber : String = intent.getStringExtra("studyRoomNumber").toString()
        var week : String = intent.getStringExtra("week").toString()

        Log.e(TAG, "Quiz Activity : studyRoomNumber : "+studyRoomNumber+", week : "+week)

        var adapter = GroupAdapter<GroupieViewHolder>()

        quizRecyclerView.apply {
            quizRecyclerView.layoutManager = LinearLayoutManager(this@QuizActivity)
            quizRecyclerView.adapter = QuizRecyclerViewAdapter(studyRoomNumber, week)
        }

        // 퀴즈 완료 버튼 클릭 시 이벤트
        quizSubmit.setOnClickListener {
            var trueCount : Int = 0
            var falseCount : Int = 0
            for (i in 0..correctAnswerList.size - 1) {
                Log.e(TAG, "correctAnswerList : " + correctAnswerList.get(i))
                if(correctAnswerList.get(i)) {
                    trueCount += 1
                } else {
                    falseCount += 1
                }
            }

            trueCount *= 20
            Log.e(TAG, "trueCount : " + trueCount + ", falseCount : " + falseCount)

            FirebaseFirestore.getInstance().collection("loginUserData")
                .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                .collection("quizScore")
                .document(week)
                .set(ScoreModel(trueCount))
                .addOnSuccessListener {
                    Log.e(TAG, trueCount.toString()+"점 저장 완료")

                    var builder = AlertDialog.Builder(this)
                    builder.setTitle("")
                    builder.setMessage("퀴즈 점수는 "+trueCount+"점 입니다.")

                    var listener = object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    finish()
                                    supportFragmentManager.beginTransaction().replace(R.id.quizActivity, TodolistFragment()).addToBackStack(null).commit()
                                }
                            }
                        }
                    }
                    builder.setPositiveButton("확인", listener)
                    builder.show()
                }
        }

    } //onCreate

    inner class QuizRecyclerViewAdapter(studyRoomNumber : String, week : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val quizList = ArrayList<QuizModel>()

        init {
            Log.e(TAG, "Quiz Activity -init : studyRoomNumber : "+studyRoomNumber+", week : "+week)

            FirebaseFirestore.getInstance().collection("studyInfo").document(studyRoomNumber)
                .collection("studyQuizWeek")
                .document(week)
                .collection("studyQuiz")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    quizList.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {
                        quizList.add(snapshot.toObject(QuizModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        } //init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.quiz_list_row, parent, false)
            return CustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            // 값 세팅하기
            holder.itemView.quizRow.text = (position + 1).toString() + "번 문제) " + quizList[position].quiz
            holder.itemView.ex1_Radio.text = quizList[position].ex1
            holder.itemView.ex2_Radio.text = quizList[position].ex2
            holder.itemView.ex3_Radio.text = quizList[position].ex3
            holder.itemView.ex4_Radio.text = quizList[position].ex4

            val answer = quizList[position].answer
            var selectAnswer : String = "0"

            holder.itemView.ex_Radio_Group.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->


                when(i) {
                    R.id.ex1_Radio -> {
                        Log.e(TAG, "1")
                        selectAnswer = "1"
                    }
                    R.id.ex2_Radio -> {
                        Log.e(TAG, "2")
                        selectAnswer = "2"
                    }
                    R.id.ex3_Radio -> {
                        Log.e(TAG, "3")
                        selectAnswer = "3"
                    }
                    R.id.ex4_Radio -> {
                        Log.e(TAG, "4")
                        selectAnswer = "4"
                    }
                }
                Log.e(TAG, "answer : "+answer+", selectAnswer : "+selectAnswer)
                if(answer == selectAnswer) {
                    correctAnswerList[position] = true
                } else {
                    correctAnswerList[position] = false
                }
            }

        } //onBindViewHolder

        override fun getItemCount(): Int {
            return quizList.size
        } //getItemCount

        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    } // class QuizRecyclerViewAdapter
} // Activity