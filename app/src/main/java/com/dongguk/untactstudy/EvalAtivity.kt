package com.dongguk.untactstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dongguk.untactstudy.Model.LoginUserData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eval.*

class EvalAtivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eval)

        val yourUid = intent.getStringExtra("yourUid").toString()
        val yourName = intent.getStringExtra("yourName").toString()

        userName.text = yourName+"님에 대한 평가를 입력하세요"

        var loginUserData = LoginUserData()

        button.setOnClickListener() {

            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("loginUserData").document(yourUid)
            docRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginUserData = task.result?.toObject(LoginUserData::class.java)!!

                        loginUserData.totalRater++
                        loginUserData.totalRating += (ratingBar1.rating + ratingBar2.rating + ratingBar3.rating) * 2

                        docRef.update("totalRater", loginUserData.totalRater)
                        docRef.update("totalRating", loginUserData.totalRating)

                        finish()
                    }
                } //addonCompleteListener


        } //button.setOnClickListener
    }
}