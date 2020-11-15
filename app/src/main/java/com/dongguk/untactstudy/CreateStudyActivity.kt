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

    var studyIndex = MutableList<String>(16) { i -> i.toString() }

    // log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_study)


        // 스터디 번호 생성을 위한 코드
        var studyNumberList = ArrayList<StudyNumberModel>()
        FirebaseFirestore.getInstance()
            .collection("studyRoomNumber")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                studyNumberList.clear()
                if(querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    studyNumberList.add(snapshot.toObject(StudyNumberModel::class.java)!!)
                }
            }

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
        var createStudyIndex = mutableListOf<EditText>()

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

                et.setHint("스터디 목차 $indexNum")
                et.id = indexNum
                 // = et.text.toString()
                linearLayout.addView(et)
                createStudyIndex.add(et)
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

                    // 모두 입력받으면 데이터 저장하기

                    for (i in 0 until 15) {
                        //createStudyIndex[i] = (linearLayout.getChildAt(i).text.toString())
                    }

                    // 스터디 룸 번호 생성 (데이터 가져와서 +1 해주기)
                    var studyNumber = studyNumberList[0].studyNumber
                    studyNumber = studyNumber + 1

                    var study = StudyModel(studyNumber, createStudyName, createStudyInfo, createStudyMemberAmount, year, month, day, first, second, "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",  uid.toString())

                    if(createStudyIndex.size > 0)
                        study.studyIndex1 = createStudyIndex[0].text.toString()
                    if(createStudyIndex.size > 1)
                        study.studyIndex2 = createStudyIndex[1].text.toString()
                    if(createStudyIndex.size > 2)
                        study.studyIndex3 = createStudyIndex[2].text.toString()
                    if(createStudyIndex.size > 3)
                        study.studyIndex4 = createStudyIndex[3].text.toString()
                    if(createStudyIndex.size > 4)
                        study.studyIndex5 = createStudyIndex[4].text.toString()
                    if(createStudyIndex.size > 5)
                        study.studyIndex6 = createStudyIndex[5].text.toString()
                    if(createStudyIndex.size > 6)
                        study.studyIndex7 = createStudyIndex[6].text.toString()
                    if(createStudyIndex.size > 7)
                        study.studyIndex8 = createStudyIndex[7].text.toString()
                    if(createStudyIndex.size > 8)
                        study.studyIndex9 = createStudyIndex[8].text.toString()
                    if(createStudyIndex.size > 9)
                        study.studyIndex10 = createStudyIndex[9].text.toString()
                    if(createStudyIndex.size > 10)
                        study.studyIndex11 = createStudyIndex[10].text.toString()
                    if(createStudyIndex.size > 11)
                        study.studyIndex12 = createStudyIndex[11].text.toString()
                    if(createStudyIndex.size > 12)
                        study.studyIndex13 = createStudyIndex[12].text.toString()
                    if(createStudyIndex.size > 13)
                        study.studyIndex14 = createStudyIndex[13].text.toString()
                    if(createStudyIndex.size > 14)
                        study.studyIndex15 = createStudyIndex[14].text.toString()
                    if(createStudyIndex.size > 15)
                        study.studyIndex16 = createStudyIndex[15].text.toString()


                    FirebaseFirestore.getInstance().collection("studyInfo")
                        .document(studyNumber.toString())
                        .set(study)
                        .addOnCompleteListener {
                            println("DB 저장 완료")

                            // DB에 새로운 스터디를 저장하면 studyRoomNumber 컬렉션에도 변경 값을 넣어줘야함
                            FirebaseFirestore.getInstance().collection("studyRoomNumber")
                                .document("studyRoomNumber")
                                .set(StudyNumberModel(studyNumber))

                            startActivity(Intent(this@CreateStudyActivity, MainActivity::class.java)) //메인 액티비티로 이동
                            finish() // 현재 액티비티 종료
                        }
                }
            }
        })
    }
}
