package com.dongguk.untactstudy.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dongguk.untactstudy.Adapter.ChatLeftYou
import com.dongguk.untactstudy.Adapter.ChatRightMe
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.ChatModel
import com.dongguk.untactstudy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoomActivity : AppCompatActivity() {

    // 보내는 사람(나)의 uid 가져오기
    private lateinit var auth : FirebaseAuth

    // FireStore
    val db = FirebaseFirestore.getInstance()

    // RealTime Database
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("message")

    // log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        auth = FirebaseAuth.getInstance()

        // ChatListActivity.kt에서 intent.putextra로 보낸 값 (대화방의 user name, uid)
        val yourUid = intent.getStringExtra("yourUid")
        val yourName = intent.getStringExtra("yourName")

        // 나의 uid 값
        val myUid = auth.uid

        var adapter = GroupAdapter<GroupieViewHolder>()

        // RealTime DB에서 message 테이블 값 가져오기
        val readRef = database.getReference("message").child(myUid.toString()).child(yourUid.toString())

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 방금 전송한 메세지를 로그에 찍음
                Log.d(TAG,"p0 : "+snapshot)

                val model = snapshot.getValue(ChatModel::class.java)
                val msg = model?.message.toString()
                val who = model?.who

                if(who == "me") {
                    adapter.add(ChatRightMe(msg))
                } else {
                    adapter.add(ChatLeftYou(msg, yourName.toString()))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        }

        recyclerView_chat.adapter = adapter
        readRef.addChildEventListener(childEventListener)

        // 마지막으로 보내는 메세지 업데이트 용
        val myRef_list = database.getReference("message-user-list")

        // 메세지 전송 버튼 클릭 시 이벤트
        send_button.setOnClickListener {

            // RealTime 데이터베이스를 사용하는 방법

            val message = chat_edittext.text.toString()
            chat_edittext.setText("")

            // 메세지를 보내는 사람
            val chat = ChatModel(
                myUid.toString(),
                yourUid.toString(),
                message,
                System.currentTimeMillis(),
                "me",
                yourName.toString()
            )
            myRef.child(myUid.toString()).child(yourUid.toString()).push().setValue(chat)

            // 메세지를 받는 사람
            val chat_get = ChatModel(
                yourUid.toString(),
                myUid.toString(),
                message,
                System.currentTimeMillis(),
                "you",
                yourName.toString()
            )
            myRef.child(yourUid.toString()).child(myUid.toString()).push().setValue(chat_get)

            // 마지막으로 보내는 메세지 업데이트 용
            myRef_list.child(myUid.toString()).child(yourUid.toString()).setValue(chat)

            chat_edittext.setText("")
        }
    }
}