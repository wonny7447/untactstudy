package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.navigation.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

//저장 버튼을 통해 DB에 분류 내용 저장

class StudyCategoryActivity: AppCompatActivity() {

    var spinner1: Spinner? = null
    var spinner2: Spinner? = null

    var dataAdapter1: ArrayAdapter<CharSequence>? = null
    var dataAdapter2: ArrayAdapter<kotlin.CharSequence?>? = null

    var first = ""
    var second = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_category)

        spinner1 = findViewById(R.id.spinner1) as Spinner
        spinner2 = findViewById(R.id.spinner2) as Spinner
        var btn = findViewById(R.id.btn) as Button

        dataAdapter1 = ArrayAdapter.createFromResource(
            this,
            R.array.study_array,
            android.R.layout.simple_spinner_item
        )
        dataAdapter1!!.setDropDownViewResource(android.R.layout.simple_spinner_item)

        spinner1?.setAdapter(dataAdapter1)
        spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (dataAdapter1!!.getItem(position) == "어학") {
                    first = "어학"
                    dataAdapter2 = ArrayAdapter.createFromResource(
                        this@StudyCategoryActivity,
                        R.array.language_array,
                        android.R.layout.simple_spinner_item
                    )
                    dataAdapter2!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    spinner2?.setAdapter(dataAdapter2)
                    spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            second = dataAdapter2!!.getItem(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                } else if (dataAdapter1!!.getItem(position) == "자격증") {
                    first = "자격증"
                    dataAdapter2 = ArrayAdapter.createFromResource(
                        this@StudyCategoryActivity,
                        R.array.certification_array,
                        android.R.layout.simple_spinner_item
                    )
                    dataAdapter2!!.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    spinner2?.setAdapter(dataAdapter2)
                    spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            second = dataAdapter2!!.getItem(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //확인 버튼
        btn.setOnClickListener(object : View.OnClickListener {
            var mRootRef = FirebaseDatabase.getInstance().reference

            override fun onClick(view: View?) {

                //1차, 2차 분류 값 DB 저장
                var fbAuth: FirebaseAuth? = null
                fbAuth = FirebaseAuth.getInstance()

                var fbDatabase = mRootRef.child("Users").child(fbAuth?.uid.toString())

                if (second != "2차 분류를 선택하세요." && second != "") {

                    var userInfo = DB__informationActivity()

                    userInfo.uid = fbAuth?.uid
                    userInfo.userId = fbAuth?.currentUser?.email
                    userInfo.first_Classification = first
                    userInfo.second_Classification = second

                    fbDatabase.setValue(userInfo).addOnCompleteListener {   //DB save
                        task ->if (task.isSuccessful) {    //DB 저장 확인
                            println("DB 저장 완료")
                            startActivity(Intent(this@StudyCategoryActivity, MainActivity::class.java)) //메인 액티비티로 이동
                        }
                    }
                }
            }
        })
    }
}

