package com.dongguk.untactstudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dongguk.untactstudy.Adapter.UserItem
import com.dongguk.untactstudy.chat.ChatRoomActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_study_member_list.*
import kotlinx.android.synthetic.main.member_list_row.view.*

class StudyMemberListActivity : AppCompatActivity() {

    //로그 변수
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_member_list)

        var adapter = GroupAdapter<GroupieViewHolder>()

        FirebaseFirestore.getInstance().collection("loginUserData")
                .whereEqualTo("studyRoomNumber", 0)
                .get()
                .addOnSuccessListener { result ->
                    for(document in result) {
                        adapter.add(UserItem(document.get("userName").toString()+"("+document.get("userEmail").toString()+")", document.get("uid").toString()))
                    }
                    member_recyclerview.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "studyRoomNumber 가져오기 실패", exception)
                } // db

        adapter.setOnItemClickListener { item, view ->

            // 채팅으로 이동
            val name = (item as UserItem).name
            val uid = (item as UserItem).uid

            /*val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("yourUid", uid)
            intent.putExtra("yourName", name)
            startActivity(intent)*/

            val intent = Intent(this, EvalAtivity::class.java)
            intent.putExtra("yourUid", uid)
            intent.putExtra("yourName", name)
            startActivity(intent)

        }

    } //onCreate


} //StudyMemberListActivity