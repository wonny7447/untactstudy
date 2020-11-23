package com.dongguk.untactstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.SuggestionData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eval.*
import kotlinx.android.synthetic.main.fragment_profile.*

class EvalAtivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eval)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val yourUid = intent.getStringExtra("yourUid").toString()
        val yourName = intent.getStringExtra("yourName").toString()

        userName.text = yourName+"님에 대한 평가를 입력하세요"

        var loginUserData = LoginUserData()
        var userData = LoginUserData()
        var suggestionData = SuggestionData()

        button.setOnClickListener() {

            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("loginUserData").document(yourUid)
            val mydocRef = db.collection("loginUserData").document(uid)

            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginUserData = task.result?.toObject(LoginUserData::class.java)!!

                        loginUserData.totalRater++
                        loginUserData.totalRating += (ratingBar1.rating + ratingBar2.rating + ratingBar3.rating) * 2 // 점수 환산 (30점 만점)

                        docRef.update("totalRater", loginUserData.totalRater) // 평가 업데이트
                        docRef.update("totalRating", loginUserData.totalRating)

                        suggestionData.suggestion = editTextTextPersonName.text.toString()
                        suggestionData.evalUid = uid

                        mydocRef
                            .get()
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful) {
                                    userData = task.result?.toObject(LoginUserData::class.java)!!

                                    suggestionData.evalUserName = userData.userName

                                }
                            }
                        suggestionData.evalTargetUid = yourUid
                        suggestionData.evalTargetName = yourName
                        suggestionData.rating = (ratingBar1.rating + ratingBar2.rating + ratingBar3.rating) * 2 // 점수 환산 (30점 만점)

                        FirebaseFirestore.getInstance().collection("studyInfo") // 건의사항 반영
                            .document(loginUserData.studyRoomNumber)
                            .collection("suggestion")
                            .document(uid)
                            .set(suggestionData)
                            .addOnSuccessListener {
                                Log.e(TAG, "Suggestion 데이터 insert 성공")

                                FirebaseFirestore.getInstance()
                                    .collection("loginUserData") // 이미 평가한 사람 리스트에 추가 (중복 평가 방지용)
                                    .document(uid)
                                    .collection("evalAlreadyPersonData")
                                    .document(yourUid)
                                    .set(suggestionData)
                                    .addOnSuccessListener {
                                        Log.e(TAG, "평가했던 사람 데이터 insert 성공")
                                    } // addonSuccessListener

                                finish()


                            } // addonSuccessListener
                    } // task.isSuccessful
                } // addOnCompleteListener
        } //button.setOnClickListener
    }
}