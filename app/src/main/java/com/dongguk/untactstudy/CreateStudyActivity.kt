package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_study.*


class CreateStudyActivity : AppCompatActivity() {

    var spinneryear: Spinner? = null
    var spinnermonth: Spinner? = null
    var spinnerday: Spinner? = null


    var spinner1: Spinner? = null
    var spinner2: Spinner? = null


    var dataAdapter1: ArrayAdapter<CharSequence>? = null
    var dataAdapter2: ArrayAdapter<CharSequence?>? = null
    var dataAdapter3: ArrayAdapter<CharSequence>? = null
    var dataAdapter4: ArrayAdapter<CharSequence>? = null
    var dataAdapter5: ArrayAdapter<CharSequence>? = null

    var first = ""
    var second = ""

    var year = "2020"
    var month = "1"
    var day = "1"

    var createStudyIndex = mutableListOf("0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_study)

        spinner1 = findViewById(R.id.createStudySpinner1stSort) as Spinner
        spinner2 = findViewById(R.id.createStudySpinner2ndSort) as Spinner
        spinneryear = findViewById(R.id.createStudySpinnerYear) as Spinner
        spinnermonth = findViewById(R.id.createStudySpinnerMonth) as Spinner
        spinnerday = findViewById(R.id.createStudySpinnerDay) as Spinner


        var btn = findViewById(R.id.createStudyButton) as Button
        var cancelbtn = findViewById(R.id.createStudyCancel) as Button
        var plusbtn = findViewById(R.id.createStudyCreateIndex) as Button

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
                        this@CreateStudyActivity,
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
                        this@CreateStudyActivity,
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

        dataAdapter3 = ArrayAdapter.createFromResource(
            this@CreateStudyActivity,
            R.array.studyFinishYear,
            android.R.layout.simple_spinner_item
        )

        dataAdapter4 = ArrayAdapter.createFromResource(
            this@CreateStudyActivity,
            R.array.studyFinishMonth,
            android.R.layout.simple_spinner_item
        )

        dataAdapter5 = ArrayAdapter.createFromResource(
            this@CreateStudyActivity,
            R.array.studyFinishDay,
            android.R.layout.simple_spinner_item
        )

        spinneryear?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                year = dataAdapter3!!.getItem(position).toString()
            }
        }

        spinnermonth?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                month = dataAdapter4!!.getItem(position).toString()
            }
        }

        spinnerday?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                day = dataAdapter5!!.getItem(position).toString()
            }
        }

        //목차 추가 버튼

        var linearLayout:LinearLayout
        linearLayout = findViewById(R.id.createStudyLinearLayout);
        var indexNum: Int = 0
        val createStudyIndex = mutableListOf(0)

        plusbtn.setOnClickListener(View.OnClickListener {

            if(indexNum < 16)
            {
                indexNum += 1

                val et = EditText(applicationContext)
                val p = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                et.layoutParams = p

                et.setHint("스터디 목차 " + indexNum)
                et.id = indexNum
                 // = et.text.toString()
                linearLayout.addView(et)
            }

        })

        //취소 버튼

        cancelbtn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {
                startActivity(Intent(this@CreateStudyActivity, MainActivity::class.java)) //메인 액티비티로 이동
                finish() // 현재 액티비티 종료
            }
        })

        //확인 버튼

        btn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(view: View?) {

                var fbAuth: FirebaseAuth? = null
                fbAuth = FirebaseAuth.getInstance()

                val uid = fbAuth.uid

                val createStudyName = createStudyName.text.toString()
                val createStudyInfo = createStudyInfo.text.toString()
                val createStudyMemberAmount = createStudyMemberAmount.text.toString()


                if (first == "1차 분류를 선택하세요." || first == "") {

                } else if (second == "2차 분류를 선택하세요." || second == "") {

                } else if (createStudyName == "") {

                } else if (createStudyInfo == "") {

                } else if (createStudyMemberAmount == "") {

                } else if (month == "2" && (day == "29") || (day == "30") || (day == "31")) {

                } else if (month == "4" && day == "31") {

                } else if (month == "6" && day == "31") {

                } else if (month == "9" && day == "31") {

                } else if (month == "11" && day == "31") {

                } else {

                    for (i in 0 until 15) {
                        //createStudyIndex[i] = (linearLayout.getChildAt(i).text.toString())
                    }

                    var study = StudyModel(createStudyName, createStudyInfo, createStudyMemberAmount, year, month, day, first, second, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", uid.toString())
                    //study.studyIndex1 = createStudyIndex[0]

                    var studyRoomNumber: Int = 999


                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("studyRoomNumber")
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    studyRoomNumber = document.get("studyRoomNumber") as Int
                                }
                            }

                    studyRoomNumber = studyRoomNumber.plus(1)

                    var studyNumber = StudyNumberModel(studyRoomNumber)

                    firestore.collection("studyRoomNumber")
                            .document("studyRoomNumber")
                            .set(studyNumber)


                    firestore.collection("studyInfo")
                            .document(studyRoomNumber.toString())
                            .set(study)
                            .addOnCompleteListener {
                                println("DB 저장 완료")
                                startActivity(Intent(this@CreateStudyActivity, MainActivity::class.java)) //메인 액티비티로 이동
                                finish() // 현재 액티비티 종료
                            }
                }
            }
        })
    }
}