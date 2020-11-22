package com.dongguk.untactstudy

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_eval.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

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

        var fbAuth = FirebaseAuth.getInstance()
        var uid = fbAuth?.uid.toString()
        var loginUserData = LoginUserData()

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
                    val totalScore = loginUserData.totalRating.toInt() / loginUserData.totalRater.toInt()
                    if (totalScore < 15) { //평가 총점이 30점 만점의 15점 미만
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
                                            tv1.text = "BUTTON_POSITIVE"
                                    }
                                }
                            }
                            builder.setPositiveButton("확인", listener)
                            builder.show()
                        } else {//그룹원일 때
                            docRef.update("studyRoomNumber", "0") //강퇴
                            docRef.update("totalRating", 0) //평가 점수 리셋
                            docRef.update("totalRater", 0) //평가 횟수 리셋

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
                                            tv1.text = "BUTTON_POSITIVE"
                                    }
                                }
                            }
                            builder.setPositiveButton("확인", listener)
                            builder.show()
                        }
                    }
                }
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.main_content, profileFragment).commit() // 프로필 (마이 페이지) 로 이동

    }

}