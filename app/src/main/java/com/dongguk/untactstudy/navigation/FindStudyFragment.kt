package com.dongguk.untactstudy.navigation

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.StudyModel
import com.dongguk.untactstudy.Model.TodoData
import com.dongguk.untactstudy.Model.studyRoomData
import com.dongguk.untactstudy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_findstudy.view.*
import kotlinx.android.synthetic.main.message_list_row.view.*
import kotlinx.android.synthetic.main.study_room_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class FindStudyFragment : Fragment(){

    var firestore: FirebaseFirestore? = null
    private val TAG = LoginActivity::class.java.simpleName

    val studyRoomList = ArrayList<studyRoomData>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_findstudy, container, false)
        firestore = FirebaseFirestore.getInstance()

        view.my_recycler_view.adapter = StudyListRecyclerViewAdapter()
        view.my_recycler_view.layoutManager = LinearLayoutManager(activity)

        val searchView = view?.findViewById<SearchView>(R.id.search_view)

        //검색어 입력시 입력 버튼 표시 여부
        searchView?.setSubmitButtonEnabled(true)

        //입력된 내용에 대한 QueryListener 추가
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyNameData = snapshot.toObject(studyRoomData::class.java)!!.studyName.toLowerCase()
                                val studyRoomNumberData = snapshot.toObject(studyRoomData::class.java)!!.studyRoomNumber.toString()
                                if (studyNameData.contains(query?.toLowerCase().toString()) || studyRoomNumberData == query)
                                    studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            //우선 모든 데이터를 가져온 다음에 studyName 에 query 가 포함되어 있거나
                            //query 와 studyRoomNumber 가 일치하는 경우만 추가시킴
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
                searchView.clearFocus() //검색 후 키보드 가림

                return true
            }

            //나중에 검색어 입력하는 도중에 추천 검색어 표시? 정도로 활용하면 될 것 같아요
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })



        val button0 = view?.findViewById<Button>(R.id.study_tag_0)

        button0?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button1 = view?.findViewById<Button>(R.id.study_tag_1)
        button1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "토익")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button2 = view?.findViewById<Button>(R.id.study_tag_2)
        button2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "토플")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button3 = view?.findViewById<Button>(R.id.study_tag_3)
        button3?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "HSK")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button4 = view?.findViewById<Button>(R.id.study_tag_4)
        button4?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "JLPT")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button5 = view?.findViewById<Button>(R.id.study_tag_5)
        button5?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "정보처리기사")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button6 = view?.findViewById<Button>(R.id.study_tag_6)
        button6?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "컴퓨터활용능력")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        val button7 = view?.findViewById<Button>(R.id.study_tag_7)
        button7?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseFirestore.getInstance()
                        .collection("studyInfo")
                        .whereEqualTo("sort2nd", "ADSP")
                        //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            studyRoomList.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot?.documents!!) {
                                studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                            }
                            (view.my_recycler_view.adapter as StudyListRecyclerViewAdapter).notifyDataSetChanged()
                        }
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        //val intent = Intent(context, FindStudyActivity::class.java)
        //startActivity(intent)

    }

    inner class StudyListRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        init{
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseFirestore.getInstance()
                    .collection("studyInfo")
                    //.whereEqualTo("uid", uid) => 나중에 스터디 고유 key 값으로 조회하기
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        studyRoomList.clear()
                        if(querySnapshot == null) return@addSnapshotListener
                        for (snapshot in querySnapshot?.documents!!) {
                            studyRoomList.add(snapshot.toObject(studyRoomData::class.java)!!)
                        }
                        notifyDataSetChanged()
                    }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.study_room_item, parent, false)

            return CustomViewHolder(view);
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val studyName = holder.itemView.textView1
            val studyInformation = holder.itemView.textView2

            var studyRoom: String

            studyName.text = studyRoomList[position].getStudyName()
            studyInformation.text = studyRoomList[position].getStudyInfo()


            var studyRoomNum = studyRoomList[position].getStudyRoomNumber().toString()

            studyRoom = studyRoomList[position].getStudyRoomNumber().toString()

            holder.itemView.apply_button.setOnClickListener {
                Log.e(TAG, "studyRoomNum : " + studyRoomNum)

                FirebaseFirestore.getInstance()
                        .collection("loginUserData")
                        .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userData = task.result?.toObject(LoginUserData::class.java)

                                var currentStudyRoom = userData?.studyRoomNumber.toString()
                                Log.e(TAG, "currentStudyRoom : " + currentStudyRoom)
                                if (currentStudyRoom == "0") {
                                    Log.e(TAG, "currentStudyRoom 이 0임")
                                    FirebaseFirestore.getInstance()
                                            .collection("studyInfo")
                                            .document(studyRoom)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                                                    val applyStudyRoom = task.result?.toObject(StudyModel::class.java)
                                                    var applyStudyRoomStartDate = sdf.parse(applyStudyRoom?.studyStartDate).time
                                                    var today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time

                                                    if (applyStudyRoomStartDate > today) {
                                                        Log.e(TAG, "지원 가능1")
                                                        FirebaseFirestore.getInstance()
                                                                .collection("loginUserData")
                                                                .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                .update("studyRoomNumber", studyRoom)
                                                                .addOnSuccessListener {
                                                                    Log.e(TAG, "loginUserData에 스터디 가입 처리 완료")

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("studyInfo")
                                                                            .document(studyRoom)
                                                                            .collection("todoList")
                                                                            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                                                if (querySnapshot == null) return@addSnapshotListener
                                                                                for (snapshot in querySnapshot?.documents!!) {
                                                                                    var TodoData = snapshot.toObject(TodoData::class.java)!!.list

                                                                                    for (i in 0..((TodoData.size) - 1)) {
                                                                                        var orientText: String = TodoData[i]
                                                                                        TodoData.set(i, "FF" + orientText)
                                                                                    }

                                                                                    FirebaseFirestore.getInstance()
                                                                                            .collection("loginUserData")
                                                                                            .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                                            .collection("todoList")
                                                                                            .document(snapshot.id)
                                                                                            .set(TodoData(TodoData))
                                                                                            .addOnSuccessListener {
                                                                                                Log.e(TAG, "loginUserData에 todoList 생성 완료")
                                                                                            }
                                                                                            .addOnFailureListener {
                                                                                                Log.e(TAG, "loginUserData에 todoList 생성 실패")
                                                                                            }
                                                                                }
                                                                            }
                                                                }
                                                                .addOnFailureListener {
                                                                    Log.e(TAG, "loginUserData에 스터디 가입 처리 실패")
                                                                }
                                                    } else {
                                                        Log.e(TAG, "스터디 지원 기간이 지나서 지원이 불가능 합니다.1")
                                                    }
                                                }
                                            }
                                }
                                else {
                                    Log.e(TAG, "currentStudyRoom 이 0이 아님")
                                    FirebaseFirestore.getInstance()
                                            .collection("studyInfo")
                                            .document(currentStudyRoom)
                                            .get()
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                                                    val currentStudyRoomData = task.result?.toObject(StudyModel::class.java)
                                                    var currentStudyRoomEndDate = sdf.parse(currentStudyRoomData?.studyEndDate).time
                                                    var today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                                                    Log.e(TAG, "현재 포함된 스터디 룸의 종료 일자" + currentStudyRoomEndDate + "현재 시간" + today)
                                                    if (currentStudyRoomEndDate < today) {
                                                        Log.e(TAG, "현재 스터디에 포함되어 있지 않음")
                                                        FirebaseFirestore.getInstance()
                                                                .collection("studyInfo")
                                                                .document(studyRoom)
                                                                .get()
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        val applyStudyRoom = task.result?.toObject(StudyModel::class.java)
                                                                        var applyStudyRoomStartDate = sdf.parse(applyStudyRoom?.studyStartDate).time

                                                                        if (applyStudyRoomStartDate > today) {
                                                                            Log.e(TAG, "지원 가능2")
                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("loginUserData")
                                                                                    .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                                    .update("studyRoomNumber", studyRoom)
                                                                                    .addOnSuccessListener {
                                                                                        Log.e(TAG, "loginUserData에 스터디 가입 처리 완료")

                                                                                        FirebaseFirestore.getInstance()
                                                                                                .collection("studyInfo")
                                                                                                .document(studyRoom)
                                                                                                .collection("todoList")
                                                                                                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                                                                    if (querySnapshot == null) return@addSnapshotListener
                                                                                                    for (snapshot in querySnapshot?.documents!!) {
                                                                                                        var TodoData = snapshot.toObject(TodoData::class.java)!!.list

                                                                                                        for (i in 0..((TodoData.size) - 1)) {
                                                                                                            var orientText: String = TodoData[i]
                                                                                                            TodoData.set(i, "FF" + orientText)
                                                                                                        }

                                                                                                        FirebaseFirestore.getInstance()
                                                                                                                .collection("loginUserData")
                                                                                                                .document(FirebaseAuth.getInstance()?.currentUser!!.uid)
                                                                                                                .collection("todoList")
                                                                                                                .document(snapshot.id)
                                                                                                                .set(TodoData(TodoData))
                                                                                                                .addOnSuccessListener {
                                                                                                                    Log.e(TAG, "loginUserData에 todoList 생성 완료")
                                                                                                                }
                                                                                                                .addOnFailureListener {
                                                                                                                    Log.e(TAG, "loginUserData에 todoList 생성 실패")
                                                                                                                }
                                                                                                    }
                                                                                                }
                                                                                    }
                                                                                    .addOnFailureListener {
                                                                                        Log.e(TAG, "loginUserData에 스터디 가입 처리 실패")
                                                                                    }
                                                                        }
                                                                        else {
                                                                            Log.e(TAG, "스터디 지원 기간이 지나서 지원이 불가능합니다.2")
                                                                        }
                                                                    }
                                                                }
                                                    }
                                                    else {
                                                        Log.e(TAG, "아직 스터디에 포함되어 있어 지원이 불가능합니다.2")
                                                    }
                                                }
                                            }
                                }
                            }
                        }
            }
            holder.itemView.textView2.setOnClickListener {

            }
        }

        override fun getItemCount(): Int {
            return studyRoomList.size
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}

