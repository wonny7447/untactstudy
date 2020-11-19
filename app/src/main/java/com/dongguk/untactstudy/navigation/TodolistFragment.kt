package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.GetStudyModel
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.TodoData
import com.dongguk.untactstudy.StudyModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_profile.*
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
    var thisWeek : Long = 0
    var currentWeek : Long = 0
    var studyRoomNumber : String = "0"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_todolist, container, false)

        var adapter = GroupAdapter<GroupieViewHolder>()
        view.todoRecyclerView.adapter = TodoRecyclerViewAdapter(currentWeek)
        view.todoRecyclerView.layoutManager = LinearLayoutManager(activity)

        // 이전 주차 데이터 확인을 위한 버튼
        var prevWeekButton = view?.findViewById<ImageButton>(R.id.prevWeekButton)

        // 이전 주차 to-do 확인을 위한 이벤트 리스너
        prevWeekButton?.setOnClickListener {
            if(currentWeek > 1) {
                currentWeek = currentWeek - 1

                view.todoRecyclerView.removeAllViewsInLayout()
                view.todoRecyclerView.adapter = TodoRecyclerViewAdapter(currentWeek)
            }
        }

        // 차주 데이터 확인을 위한 버튼
        var nextWeekButton = view?.findViewById<ImageButton>(R.id.nextWeekButton)

        // 차주 to-do 확인을 위한 이벤트 리스너
        nextWeekButton?.setOnClickListener {
            Log.e(TAG, "차주 데이터 노출")
        }

        return view
    } // onCreateView

    inner class TodoRecyclerViewAdapter(temp : Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //var todoList : MutableList<String> = listOf<String>("") as MutableList<String>
        var todoList = ArrayList<String>()


        init {
            data(temp)

        } // init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_row, parent, false)
            return TodoCustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val todoText = holder.itemView.todoText

            thisWeekText.text = currentWeek.toString()+"주차 TO-DO"

            // todo_list_row의 todoText에 값 보여주기
            todoText.text = todoList[position].toString()
            Log.e(TAG, "position : "+ position+", todoText.text : "+todoText.text)

            // todo_list_row에 대한 클릭 이벤트
            holder.itemView.setOnClickListener{
                Log.e(TAG, "click : "+position)
            }

            // todo_list_row의 체크박스에 대한 클릭 이벤트
            holder.itemView.checkBox.setOnCheckedChangeListener{ compoundButton: CompoundButton, isChecked: Boolean ->
                if(isChecked) {
                    // 사용자 DB > to-do 리스트에서 해당 항목 true로 변경
                    Log.e(TAG, "click : "+position+", checked")
                } else {
                    // 사용자 DB > to-do 리스트에서 해당 항목 false로 변경
                    Log.e(TAG, "click : "+position+", UN checked")
                }
            }

        } //onBindViewHolder

        override fun getItemCount(): Int {
            return todoList.size
        } //getItemCount

        fun data(temp : Long){
            var userData : LoginUserData ? = null
            var studyData : StudyModel ?= null
            var week = temp

            FirebaseFirestore.getInstance().collection("loginUserData")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnCompleteListener {
                        task ->
                    if(task.isSuccessful) {
                        userData = task.result?.toObject(LoginUserData::class.java)
                        studyRoomNumber = userData?.studyRoomNumber.toString()
                        Log.e(TAG, "studyRoomNumber : "+studyRoomNumber)

                        // 가입 스터디 유무 조회
                        if(studyRoomNumber == null || studyRoomNumber == "0") {
                            // 가입한 스터디가 없으면 (null or 0) 스터디 추천으로 이동
                            // Alert : 스터디에 가입해주세요.
                            var intent = Intent(context, FindStudyFragment::class.java)
                            startActivity(intent)
                        } else {
                            // 가입한 스터디가 있으면 조건 체크해서 to-do List 노출하기
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
                                        //var thisWeek : Long = 0
                                        Log.e(TAG, "studyStartDate : "+studyStartDate+", today : "+today)

                                        if(studyStartDate > today) {
                                            // Alert : 스터디가 시작하지 않았습니다. yyyy-mm-dd부터 to-do 조회가 가능합니다.
                                            var intent = Intent(context, FindStudyFragment::class.java)
                                            startActivity(intent)
                                        } else {
                                            var diff : Long = studyStartDate - today
                                            diff /= (24 * 60 * 60 * 1000)
                                            diff = Math.abs(diff)
                                            if(thisWeek < 1) {
                                                if ((diff % 7) > 0) {
                                                    thisWeek = (diff / 7) + 1
                                                } else {
                                                    thisWeek = (diff / 7)
                                                }
                                                week = thisWeek
                                            }
                                            Log.e(TAG, "diff : "+diff+", thisWeek : "+thisWeek+", currentWeek : "+currentWeek+", week : "+week)

                                            FirebaseFirestore.getInstance()
                                                .collection("studyInfo")
                                                .document(studyRoomNumber)
                                                .collection("todoList")
                                                .document(week.toString())
                                                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                                    Log.e(TAG, "db week : "+week+", ")
                                                    todoList.clear()
                                                    if(querySnapshot == null) return@addSnapshotListener
                                                    Log.e(TAG, "querySnapShot : "+querySnapshot.toObject(TodoData::class.java)!!.list.toString())
                                                    var tempList = querySnapshot.toObject(TodoData::class.java)!!.list
                                                    for(i in 1 .. tempList.size) {
                                                        todoList.add(tempList[i-1].toString())
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
        }

        inner class TodoCustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    } // class : TodoRecyclerViewAdapter
}