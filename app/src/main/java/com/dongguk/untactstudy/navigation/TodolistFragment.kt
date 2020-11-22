package com.dongguk.untactstudy.navigation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.MainActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.TodoData
import com.dongguk.untactstudy.StudyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_todolist.*
import kotlinx.android.synthetic.main.fragment_todolist.view.*
import kotlinx.android.synthetic.main.todo_list_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodolistFragment : Fragment(){

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    var studyStartDate : Long = 0
    var studyEndDate : Long = 0
    var thisWeek : Long = 0
    var currentWeek : Long = 0
    var studyRoomNumber : String = "0"

    private val mFragmentManager = fragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_todolist, container, false)

        var adapter = GroupAdapter<GroupieViewHolder>()
        view.todoRecyclerView.adapter = TodoRecyclerViewAdapter(currentWeek)
        view.todoRecyclerView.layoutManager = LinearLayoutManager(activity)

        // 이전 주차 to-do 확인을 위한 버튼과 이벤트 리스너
        var prevWeekButton = view?.findViewById<ImageButton>(R.id.prevWeekButton)
        prevWeekButton?.setOnClickListener {
            if(currentWeek > 1) {
                currentWeek = currentWeek - 1

                view.todoRecyclerView.removeAllViewsInLayout()
                view.todoRecyclerView.adapter = TodoRecyclerViewAdapter(currentWeek)
            }
        }

        // 차주 to-do 확인을 위한 버튼과 이벤트 리스너
        var nextWeekButton = view?.findViewById<ImageButton>(R.id.nextWeekButton)
        nextWeekButton?.setOnClickListener {

            // 스터디 그룹의 데이터를 기준으로 마지막 주의 값을 구하기 위한 연산
            var endWeek : Long = 0
            var diffEnd : Long = studyStartDate - studyEndDate
            diffEnd /= (24 * 60 * 60 * 1000)
            diffEnd = Math.abs(diffEnd)

            if((diffEnd % 7) > 0) {
                endWeek = (diffEnd / 7) + 1
            } else {
                endWeek = diffEnd / 7
            }

            // 현재 보여주는 to do 리스트의 주차 수 값이 마지막 주차보다 작을때만 리스트 새로 보여줌
            if(currentWeek < endWeek) {
                currentWeek = currentWeek + 1

                view.todoRecyclerView.removeAllViewsInLayout()
                view.todoRecyclerView.adapter = TodoRecyclerViewAdapter(currentWeek)
            }
        }

        return view
    } // onCreateView

    inner class TodoRecyclerViewAdapter(temp : Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var todoList = ArrayList<String>()
        var myUid : String = FirebaseAuth.getInstance()?.currentUser!!.uid.toString()

        init {
            data(temp)
        } // init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_row, parent, false)
            return TodoCustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val todoText = holder.itemView.todoText
            val checkImage = holder.itemView.checkImage

            // 선택한 주차 수 보여주기
            thisWeekText.text = currentWeek.toString()+"주차 TO-DO"

            // todo_list_row의 todoText에 값 보여주기
            var subText = todoList[position].toString().substring(0,1)
            var realText = todoList[position].toString().substringAfterLast(subText)

            // 원래 텍스트 데이터에는 T/F 값이 포함되어 있기 때문에 흰색으로 설정했다가 블랙으로 변경
            todoText.setTextColor(Color.BLACK)

            // T/F 값을 제거한 진짜 to-do 데이터
            todoText.text = realText

            // T/F를 제외한 텍스트 값이 없는 경우 row를 안보이게 처리함
            // 실제 데이터가 null일 수는 없고, 개수 맞추기 위한 임시 데이터에 대한 처리임
            if(realText == null || realText == "") {
                holder.itemView.visibility = View.GONE
            }

            // T/F 값에 따라 체크이미지, 체크 없는 이미지 보여줌
            if(subText == "T") {
                checkImage.setImageResource(android.R.drawable.checkbox_on_background)
            } else if (subText == "F"){
                checkImage.setImageResource(android.R.drawable.checkbox_off_background)
            }

            // todo_list_row에 대한 클릭 이벤트
            holder.itemView.setOnClickListener{
                if(subText == "T") {
                    todoList[position] = "F" + realText
                    checkImage.setImageResource(android.R.drawable.checkbox_off_background)
                } else {
                    todoList[position] = "T" + realText
                    checkImage.setImageResource(android.R.drawable.checkbox_on_background)
                }
                chekBoxValueUpdate()
            } //setOnClickListener
        } //onBindViewHolder

        // T/F 값 변경하는 쿼리
        fun chekBoxValueUpdate() {
            FirebaseFirestore.getInstance()
                .collection("loginUserData")
                .document(myUid)
                .collection("todoList")
                .document(currentWeek.toString())
                .set(TodoData(todoList))
                .addOnSuccessListener {
                    Log.e(TAG, "checkbox 값 변경 완료")
                }
        }

        override fun getItemCount(): Int {
            return todoList.size
        } //getItemCount

        // 실제 to do 리스트에 보여주는 데이터를 불러오는 로직
        fun data(temp : Long){
            var userData : LoginUserData ? = null
            var studyData : StudyModel ?= null
            var week = temp

            // 1. 로그인 정보를 기준으로 가입한 스터디 정보를 가져온다.
            FirebaseFirestore.getInstance().collection("loginUserData")
                .document(myUid)
                .get()
                .addOnCompleteListener {
                    task ->
                    if(task.isSuccessful) {
                        userData = task.result?.toObject(LoginUserData::class.java)
                        studyRoomNumber = userData?.studyRoomNumber.toString()
                        Log.e(TAG, "studyRoomNumber : "+studyRoomNumber)

                        // 1-1. 가입 스터디 유무 조회
                        if(studyRoomNumber == null || studyRoomNumber == "0") {
                            // 가입한 스터디가 없으면 (null or 0) 스터디 추천으로 이동
                            noticeAndSwitch("스터디에 가입해주세요.", TodolistFragment(), FindStudyFragment())
                        } else {
                            // 2. 가입한 스터디가 있으면 현재 주차 수를 확인해서 to-do List 노출한다.
                            FirebaseFirestore.getInstance()
                                .collection("studyInfo")
                                .document(studyRoomNumber)
                                .get()
                                .addOnCompleteListener {
                                    task ->
                                    if(task.isSuccessful) {
                                        studyData = task.result?.toObject(StudyModel::class.java)
                                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                                        var today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time

                                        studyStartDate = sdf.parse(studyData?.studyStartDate.toString()).time
                                        studyEndDate = sdf.parse(studyData?.studyEndDate.toString()).time

                                        Log.e(TAG, "studyStartDate : "+studyStartDate+", today : "+today)

                                        // 2-1. 스터디시작일, 종료일, 현재날짜를 기반으로 스터디 조회할 수 있는 기준이 각각 다름
                                        /* 1) 스터디 시작 전/후: to do 리스트를 볼 수 없음
                                        *  2) 스터디 시작
                                        * */
                                        if(studyStartDate > today) {
                                            val msg : String = "스터디가 시작하지 않았습니다.\n"+ studyData?.studyStartDate.toString()+"부터 to-do 조회가 가능합니다."
                                            noticeAndSwitch(msg, TodolistFragment(), MyStudyFragment())
                                        } else {
                                            var diff : Long = studyStartDate - today
                                            diff /= (24 * 60 * 60 * 1000)
                                            diff = Math.abs(diff)

                                            if(thisWeek < 1) {
                                                if ((diff % 7) > 0) {
                                                    thisWeek = (diff / 7) + 1
                                                } else if(diff < 7) {
                                                    thisWeek = 1
                                                } else {
                                                    thisWeek = (diff / 7)
                                                }
                                                week = thisWeek
                                            }
                                            Log.e(TAG, "diff : "+diff+", thisWeek : "+thisWeek+", currentWeek : "+currentWeek+", week : "+week)

                                            FirebaseFirestore.getInstance()
                                                .collection("loginUserData")
                                                .document(myUid)
                                                .collection("todoList")
                                                .document(week.toString())
                                                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                    Log.e(TAG, "데이터 불러오는 부분 db week : "+week+", ")
                                                    todoList.clear()        // 기존에 있던 값을 지움

                                                    if(querySnapshot == null)
                                                        return@addSnapshotListener
                                                    Log.e(TAG, "데이터 불러오는 부분 querySnapShot : "+querySnapshot.toObject(TodoData::class.java)!!.list.toString())

                                                    var tempList = querySnapshot.toObject(TodoData::class.java)!!.list

                                                    // 리스트에 값이 들어있기 때문에, 사이즈만큼 반복하여 각 항목에 있는 값을 가져옴
                                                    for (i in 1..tempList.size) {
                                                        todoList.add(tempList[i - 1])
                                                        notifyDataSetChanged()
                                                    }
                                                    currentWeek = week
                                                } //addSnapshotListener
                                        } //if-else
                                    } //if(task.isSuccessful)
                                } //addOnCompleteListener
                        }  //if-else
                    } //if(task.isSuccessful)
                } //addOnCompleteListener
        } // fun data()

        // 파라미터에 따라서 알럿을 보여주고 화면 전환 시키는 function
        // 화면전환은 fragment -> fragment만 가능
        fun noticeAndSwitch(alertMessage : String, nowFrag : Fragment, afterFrag : Fragment) {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("")
            builder.setMessage(alertMessage)

            var listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            fragmentManager?.beginTransaction()?.remove(nowFrag)?.commit()
                            fragmentManager?.beginTransaction()?.replace(R.id.main_content, afterFrag)?.commit()
                        }
                    }
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.show()
        } //noticeAndSwitch

        inner class TodoCustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    } // class : TodoRecyclerViewAdapter
} // Activity