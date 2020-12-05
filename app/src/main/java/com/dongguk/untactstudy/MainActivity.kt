package com.dongguk.untactstudy

import com.dongguk.untactstudy.R

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.ScoreModel
import com.dongguk.untactstudy.Model.StudyModel
import com.dongguk.untactstudy.Model.TodoCountData
import com.dongguk.untactstudy.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eval.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    var totalQuizScore : Float = 0f
    var totalTodoScore : Float = 0f
    var totalScore : Float = 0f
    var thisWeek : Long = 0
    val TAG = LoginActivity::class.java.simpleName

    var fbAuth = FirebaseAuth.getInstance()
    var uid = fbAuth?.uid.toString()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_findstudy -> {
                var findStudyFragment = FindStudyFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, findStudyFragment).commit() // 스터디 추천 및 찾기로 이동
                return true
            }
            R.id.action_todolist -> {
                var todolistFragment = TodolistFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, todolistFragment).commit() // 해야할 일로 이동
                return true
            }
            R.id.action_mystudy -> {
                var myStudyFragment = MyStudyFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, myStudyFragment).commit() // 현재 가입해 있는 스터디 페이지 (채팅 포함) 로 이동
                return true
            }
            R.id.action_chatting -> {
                var settingsFragment = ChattingFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, settingsFragment).commit() // 설정이 아니라, 개인 채팅으로 이동
                return true
             }
            R.id.action_profile -> {
                var profileFragment = ProfileFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, profileFragment).commit() // 프로필 (마이 페이지) 로 이동

                return true
            }

        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        var profileFragment = ProfileFragment()

        var loginUserData = LoginUserData()

            CalculateScore()



        supportFragmentManager.beginTransaction().replace(R.id.main_content, profileFragment).commit() // 프로필 (마이 페이지) 로 이동

    }

    fun CalculateScore() {

        val TAG = LoginActivity::class.java.simpleName
        var fbAuth = FirebaseAuth.getInstance()
        var myUid = fbAuth?.uid.toString()
        var userData : LoginUserData? = null
        var studyData : StudyModel? = null
        var quizScoreData : ScoreModel? = null
        var todoCountData : TodoCountData? = null
        var studyRoomNumber = "0"
        var studyStartDate : Long = 0
        var studyEndDate : Long = 0
        var totalTodo : Long = 0
        var completeTodo : Long = 0

        FirebaseFirestore.getInstance().collection("loginUserData")
            .document(myUid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userData = task.result?.toObject(LoginUserData::class.java)
                    studyRoomNumber = userData?.studyRoomNumber.toString()
                    Log.e(TAG, "studyRoomNumber : " + studyRoomNumber)

                    // 1-1. 가입 스터디 유무 조회
                    if (studyRoomNumber == null || studyRoomNumber == "0") {
                        // 가입한 스터디가 없으면 점수를 계산하지 않음

                    } else {
                        // 2. 가입한 스터디가 있으면 현재 주차 수를 확인한다.
                        FirebaseFirestore.getInstance()
                            .collection("studyInfo")
                            .document(studyRoomNumber)
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    studyData = task.result?.toObject(StudyModel::class.java)
                                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                                    var today =
                                        sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time

                                    studyStartDate =
                                        sdf.parse(studyData?.studyStartDate.toString()).time
                                    studyEndDate =
                                        sdf.parse(studyData?.studyEndDate.toString()).time

                                    Log.e(TAG, "studyStartDate : " + studyStartDate + ", today : " + today)

                                        if(studyStartDate > today) {
                                            // 스터디가 시작하지 않았을 때에는 Todo점수와 퀴즈 점수를 만점 처리 (따라서, 강퇴 불가)
                                            totalTodoScore = 20f
                                            totalQuizScore = 50f
                                            totalScore = totalQuizScore + totalTodoScore
                                            ScoreCheck()
                                        } else {
                                            var diff: Long = studyStartDate - today
                                            diff /= (24 * 60 * 60 * 1000)
                                            diff = Math.abs(diff)

                                            if (thisWeek < 1) {
                                                if ((diff % 7) > 0) {
                                                    thisWeek = (diff / 7) + 1
                                                } else if (diff < 7) {
                                                    thisWeek = 1
                                                } else {
                                                    thisWeek = (diff / 7)
                                                }
                                            }
                                            Log.e(TAG, "diff : " + diff + ", thisWeek : " + thisWeek)

                                            for(i in 1..thisWeek) {
                                                //퀴즈 점수를 불러온다
                                                FirebaseFirestore.getInstance()
                                                    .collection("loginUserData")
                                                    .document(myUid)
                                                    .collection("quizScore")
                                                    .document(i.toString())
                                                    .get()
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            quizScoreData = task.result?.toObject(ScoreModel::class.java)
                                                            totalQuizScore += quizScoreData?.score!!
                                                        }
                                                    }
                                                //to-do 완료율을 불러온다
                                                FirebaseFirestore.getInstance()
                                                    .collection("loginUserData")
                                                    .document(myUid)
                                                    .collection("todoCount")
                                                    .document(i.toString())
                                                    .get()
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            todoCountData = task.result?.toObject(TodoCountData::class.java)
                                                            totalTodo += todoCountData?.totalCount!!
                                                            completeTodo += todoCountData?.completeCount!!
                                                        }//task.isSuccessful
                                                    }//addOnCompleteListener
                                            }//for

                                            FirebaseFirestore.getInstance()
                                                .collection("loginUserData")
                                                .document(myUid)
                                                .collection("todoCount")
                                                .document(1.toString())
                                                .get()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                        totalQuizScore /= thisWeek
                                                        totalQuizScore /= 2
                                                        totalTodoScore = completeTodo / totalTodo * 20f
                                                        totalScore = totalQuizScore + totalTodoScore
                                                        ScoreCheck()
                                                    }//task.isSuccessful
                                                }//addOnCompleteListener
                                        }//if-else
                                }//task.isSuccessful
                            }//addOnCompleteListener
                    }//if-else
                }//task.isSuccessful
            }//addOnCompleteListener
    }//fun CalculateScore

    fun ScoreCheck()
    {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("loginUserData").document(uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var loginUserData = task.result?.toObject(LoginUserData::class.java)!!
                val leader = loginUserData.leader
                val totalRater = loginUserData.totalRater.toInt()

                if (totalRater == 0 || totalRater == null) //평가 받은 기록 없음
                    ;
                else {//평가 받은 기록 있음
                    val totalRatingScore = loginUserData.totalRating.toInt() / loginUserData.totalRater.toInt()
                    totalScore += totalRatingScore
                    Log.e(TAG, "Total Score : " + totalScore)
                    if (totalScore < 50 && thisWeek > 1) { //평가 총점이 30점 만점의 15점 미만
                        if (leader == true) {  //스터디그룹 팀장일 때
                            //경고 다이얼로그
                            var builder = AlertDialog.Builder(this)
                            builder.setTitle("경고 알림")
                            builder.setMessage("가입된 스터디 그룹에서 회원님의 멤버 평가 점수가 저조하여, 경고 처리되셨음을 알려드립니다.\n")
                            builder.setIcon(R.mipmap.ic_launcher)

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->
                                            tv1.text = " "
                                    }
                                }
                            }
                            builder.setPositiveButton("확인", listener)
                            builder.show()
                        } else {//그룹원일 때
                            docRef.update("studyRoomNumber", "0") //강퇴
                            docRef.update("totalRating", 0) //평가 점수 리셋
                            docRef.update("totalRater", 0) //평가 횟수 리셋

                            docRef
                                .collection("evalAlreadyPersonData")
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        docRef.collection("evalAlreadyPersonData").document(document.id)
                                            .delete()
                                    }
                                }

                            docRef
                                .collection("todoList")
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        docRef.collection("todoList").document(document.id)
                                            .delete()
                                    }
                                }

                            //강퇴 다이얼로그
                            var builder = AlertDialog.Builder(this)
                            builder.setTitle("강제 퇴장 알림")
                            builder.setMessage("가입된 스터디 그룹에서 회원님의 멤버 평가 점수가 저조하여, 강제 퇴장 되셨음을 알려드립니다.\n")
                            builder.setIcon(R.mipmap.ic_launcher)

                            // 버튼 클릭시에 무슨 작업을 할 것인가!
                            var listener = object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    when (p1) {
                                        DialogInterface.BUTTON_POSITIVE ->
                                            tv1.text = " "
                                    }
                                }
                            }
                            builder.setPositiveButton("확인", listener)
                            builder.show()
                        }//if-else
                    }//if
                }//if-else
            }//task.isSuccessful
        }//addOnCompleteListener
    }//fun ScoreCheck

}//class MainActivity