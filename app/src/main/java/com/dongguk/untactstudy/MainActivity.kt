package com.dongguk.untactstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.dongguk.untactstudy.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
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

                /*
                // 네트워크 상태에 따라 공부 여부를 결정함
                NetworkStatus(this, {
                    //네트워크 연결됨
                    val profileFragment: ProfileFragment =
                        supportFragmentManager.findFragmentById(R.id.main_content) as ProfileFragment
                    profileFragment.changeFragmentTextView("Study ON")

                }, {
                    //네트워크 연결 끊김
                    val profileFragment: ProfileFragment =
                        supportFragmentManager.findFragmentById(R.id.main_content) as ProfileFragment
                    profileFragment.changeFragmentTextView("Study OFF")
                })
                */

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
        supportFragmentManager.beginTransaction().replace(R.id.main_content, profileFragment).commit() // 프로필 (마이 페이지) 로 이동

        /*
        NetworkStatus(this, {
            //네트워크 연결됨
            val profileFragment: ProfileFragment =
                supportFragmentManager.findFragmentById(R.id.main_content) as ProfileFragment
            profileFragment.changeFragmentTextView("Study ON")

        }, {
            //네트워크 연결 끊김
            val profileFragment: ProfileFragment =
                supportFragmentManager.findFragmentById(R.id.main_content) as ProfileFragment
            profileFragment.changeFragmentTextView("Study OFF")
        })
        */

    }
}