package com.dongguk.untactstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_eval.*

class EvalAtivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eval)

        val yourUid = intent.getStringExtra("yourUid").toString()
        val yourName = intent.getStringExtra("yourName").toString()

        userName.text = yourName+"님에 대한 평가를 입력하세요"
    }
}