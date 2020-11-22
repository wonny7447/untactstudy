package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_study.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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

    var year = SimpleDateFormat("yyyy").format(Date())
    var month = SimpleDateFormat("MM").format(Date())
    var day = SimpleDateFormat("dd").format(Date())

    var studyIndex = MutableList<String>(16) { i -> i.toString() }

    // log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_study)
        setTitle("스터디 만들기")

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
                    // 스터디 룸 번호 생성 (데이터 가져와서 +1 해주기)
                    var studyNumber = studyNumberList[0].studyNumber
                    studyNumber = studyNumber + 1


                    val cal : Calendar = Calendar.getInstance()
                    cal.set(Calendar.YEAR, SimpleDateFormat("yyyy").format(Date()).toInt())
                    cal.set(Calendar.MONTH, SimpleDateFormat("MM").format(Date()).toInt() - 1)
                    cal.set(Calendar.DATE, SimpleDateFormat("dd").format(Date()).toInt())
                    cal.add(Calendar.DATE, 7)

                    var studyStartDate : String = SimpleDateFormat("yyyy-MM-dd").format(cal.time)

                    Log.e(TAG, "year : "+SimpleDateFormat("yyyy").format(Date()).toInt()+", month : "+SimpleDateFormat("MM").format(Date()).toInt()+", date : "+SimpleDateFormat("dd").format(Date()).toInt()+", studyStartDate : "+studyStartDate)

                    var study = StudyModel(
                        studyNumber,
                        createStudyName,
                        createStudyInfo,
                        createStudyMemberAmount,
                        first, second,
                        uid.toString(),
                        SimpleDateFormat("yyyy-MM-dd").format(Date()),
                        studyStartDate,
                        year+"-"+month+"-"+day
                    )

                    FirebaseFirestore.getInstance().collection("studyInfo")
                        .document(studyNumber.toString())
                        .set(study)
                        .addOnCompleteListener {
                            println("스터디 생성 정보 DB 저장 완료")

                            // DB에 새로운 스터디를 저장하면 studyRoomNumber 컬렉션에도 변경 값을 넣어줘야함
                            FirebaseFirestore.getInstance().collection("studyRoomNumber")
                                .document("studyRoomNumber")
                                .set(StudyNumberModel(studyNumber))

                            // 스터디 생성자는 해당 스터디에 가입하고, 리더가 되게 바꿈
                            FirebaseFirestore.getInstance()
                                .collection("loginUserData")
                                .document(uid.toString())
                                .update("leader", true, "studyRoomNumber", studyNumber.toString())
                                .addOnCompleteListener {
                                    Log.e(TAG, "스터디 생성자의 loginUserData 업데이트 성공")
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, "스터디 생성자의 loginUserData 업데이트 실패")
                                }


                            // 목차 만드는 페이지로 넘겨야 하는 정보
                            Log.e(TAG, "studyStartDate : "+studyStartDate+", endDate : "+year+"-"+month+"-"+day)

                            var intent = Intent(this@CreateStudyActivity, CreateTodoActivity::class.java)
                            intent.putExtra("studyNumber", studyNumber.toString())
                            intent.putExtra("studyStartDate", studyStartDate)
                            intent.putExtra("studyEndDate", year+"-"+month+"-"+day)
                            startActivity(intent)
                            finish() // 현재 액티비티 종료
                        }
                }
            }
        })
    }
}
