package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.Model.LoginUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setting.*

class profile_setting : AppCompatActivity() {

    //로그 변수
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        var UserData : LoginUserData ?= null

        FirebaseFirestore.getInstance()
            .collection("loginUserData")
            .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
            .get()
            .addOnCompleteListener {
                task ->
                if(task.isSuccessful) {
                    UserData = task.result?.toObject(LoginUserData::class.java)
                    Log.e(TAG, "userName : "+UserData?.userName.toString())
                    user_name.text = UserData?.userName.toString()
                    user_email.text = UserData?.userEmail.toString()
                    user_info.hint = UserData?.introduction.toString()
                    Glide.with(this).load(UserData?.userPhotoUrl).into(profile_image)
                }
            } //addOnCompleteListener

        mystudy_insert.setOnClickListener {
            val newintrduction = user_info.text.toString()

            val db = FirebaseFirestore.getInstance().collection("loginUserData")
            db.document(FirebaseAuth.getInstance().uid.toString())
                .update("introduction", newintrduction)
                .addOnCompleteListener {
                    Log.e(TAG, "DB에 사용자 설명 추가 성공")
                    startActivity(Intent(this@profile_setting, MainActivity::class.java))
                }
                .addOnFailureListener {
                    Log.e(TAG, "DB에 사용자 설명 추가 성공")
                }

        }
    }
}