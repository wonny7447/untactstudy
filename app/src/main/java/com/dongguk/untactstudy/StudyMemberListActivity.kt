package com.dongguk.untactstudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.SuggestionData
import com.dongguk.untactstudy.chat.ChatRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_eval.*
import kotlinx.android.synthetic.main.activity_study_member_list.*
import kotlinx.android.synthetic.main.member_list_row.view.*
import kotlinx.android.synthetic.main.message_list_row.*

class StudyMemberListActivity : AppCompatActivity() {

    //로그 변수
    private val TAG = LoginActivity::class.java.simpleName
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    var myStudyRoomNumber : String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_member_list)

        myStudyRoomNumber = intent.getStringExtra("myStudyRoomNumber").toString()

        FirebaseFirestore.getInstance()
                .collection("studyInfo")
                .document(myStudyRoomNumber)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        var studyData = task.result?.toObject(StudyModel::class.java)
                        var studyName = studyData?.studyName
                        setTitle(studyName+" 스터디 멤버")
                    }
                }

        var adapter = GroupAdapter<GroupieViewHolder>()

        member_recyclerview.apply {
            member_recyclerview.layoutManager = LinearLayoutManager(this@StudyMemberListActivity)
            member_recyclerview.adapter = MemberRecyclerViewAdapter()
        }

    } //onCreate

    inner class MemberRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val memberList = ArrayList<LoginUserData>()

        init {
            FirebaseFirestore.getInstance()
                    .collection("loginUserData")
                    .whereEqualTo("studyRoomNumber", myStudyRoomNumber)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        memberList.clear()
                        if(querySnapshot == null) return@addSnapshotListener
                        for (snapshot in querySnapshot?.documents!!) {
                            if((snapshot.toObject(LoginUserData::class.java)?.uid) != uid) {
                                memberList.add(snapshot.toObject(LoginUserData::class.java)!!)
                            }
                        }
                        notifyDataSetChanged()
                    }
        } //init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.member_list_row, parent, false)
            return CustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val member_name = holder.itemView.member_name
            val member_uid = holder.itemView.member_uid
            val chat_button = holder.itemView.chat_button
            val evaluation_button = holder.itemView.evaluation_button

            member_name.text = memberList[position].userName
            member_uid.text = memberList[position].uid

            chat_button.setOnClickListener {
                Log.e(TAG, "채팅 버튼 클릭 - member_name : "+member_name.text)
                val intent = Intent(this@StudyMemberListActivity, ChatRoomActivity::class.java)
                intent.putExtra("yourUid", member_uid.text.toString())
                intent.putExtra("yourName", member_name.text.toString())
                startActivity(intent)
            }

            evaluation_button.setOnClickListener {
                Log.e(TAG, "평가 버튼 클릭 - member_name : "+member_name.text)

                val db = FirebaseFirestore.getInstance()
                val evalAlreadyDocument = db.collection("loginUserData").document(uid).collection("evalAlreadyPersonData")
                var isEvalAlready = false


                evalAlreadyDocument
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if(memberList[position].uid == document.id)
                            {
                                isEvalAlready = true
                            }
                        }
                        when {
                            uid == memberList[position].uid -> {
                                Toast.makeText(applicationContext, "자기 자신을 평가할 수 없습니다.", Toast.LENGTH_LONG).show()
                            }
                            isEvalAlready -> {
                                Toast.makeText(applicationContext, "이미 평가한 상대입니다.", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                val intent = Intent(this@StudyMemberListActivity, EvalAtivity::class.java)
                                intent.putExtra("yourUid", memberList[position].uid)
                                intent.putExtra("yourName", memberList[position].userName)
                                startActivity(intent)
                            }
                        }
                    }


            }

        } //onBindViewHolder

        override fun getItemCount(): Int {
            return memberList.size
        } //getItemCount

        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    } // class : MemberRecyclerViewAdapter

} //StudyMemberListActivity
