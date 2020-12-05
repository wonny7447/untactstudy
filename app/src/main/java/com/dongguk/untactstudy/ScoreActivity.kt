package com.dongguk.untactstudy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.ScoreModel
import com.dongguk.untactstudy.Model.StudyModel
import com.dongguk.untactstudy.Model.TodoCountData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_score.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class ScoreActivity  : AppCompatActivity() {

    val TAG = LoginActivity::class.java.simpleName
    var fbAuth = FirebaseAuth.getInstance()
    var uid = fbAuth?.uid.toString()

    var thisWeek : Long = 0

    var totalQuizScore : Float = 0f
    var totalTodoScore : Float = 0f
    var totalRatingScore : Float = 0f
    var totalScore : Float = 0f
    var isNoRate : Boolean = false
    var isNotStart : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        CalculateScore()
    }

    fun ShowScore()
    {
        totalQuizScore = floor(totalQuizScore * 100) / 100
        totalTodoScore = floor(totalTodoScore * 100) / 100
        totalRatingScore = floor(totalRatingScore * 100) / 100
        totalScore = floor(totalScore * 100) / 100


        if(isNotStart)
        {
            todoscore.text = "스터디가 시작하지 않았습니다"
            quizscore.text = "스터디가 시작하지 않았습니다"
            ratingscore.text = "스터디가 시작하지 않았습니다"
            score.text = "측정 불가"
        }
        else
        {
            if(isNoRate)
            {
                score.text = "측정 불가"
                ratingscore.text = "평가 데이터가 없습니다"
            }
            else
            {
                score.text = totalScore.toString() + "점"
                ratingscore.text = totalRatingScore.toString() + "점 / 30점"
            }
            todoscore.text = totalTodoScore.toString() + "점 / 20점"
            quizscore.text = totalQuizScore.toString() + "점 / 50점"

        }
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
                                        isNotStart = true
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

                                        for(i in 1 until thisWeek) {
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
                                            //todo 완료율을 불러온다 (글씨 상태가?)
                                            FirebaseFirestore.getInstance()
                                                .collection("loginUserData")
                                                .document(myUid)
                                                .collection("todoCount")
                                                .document(i.toString())
                                                .get()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        todoCountData = task.result?.toObject(
                                                            TodoCountData::class.java)
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

                if (totalRater == 0 || totalRater == null) {//평가 받은 기록 없음
                    isNoRate = true
                    ShowScore()
                }
                else {//평가 받은 기록 있음
                    val totalRatingScore = loginUserData.totalRating.toInt() / loginUserData.totalRater.toInt()
                    totalScore += totalRatingScore
                    Log.e(TAG, "Total Score : " + totalScore)
                    ShowScore()

                }//if-else
            }//task.isSuccessful
        }//addOnCompleteListener
    }//fun ScoreCheck
}

