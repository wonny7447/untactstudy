// 채팅 메인 화면
package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.chat.ChatRoomActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_chatting.*
import kotlinx.android.synthetic.main.fragment_chatting.view.*
import kotlinx.android.synthetic.main.message_list_row.view.*

class ChattingFragment : Fragment(){

    // Logout
    private lateinit var auth : FirebaseAuth

    // Chat
    val fsDB = FirebaseFirestore.getInstance()
    val rtDB = FirebaseDatabase.getInstance()

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_chatting, container, false)
        var adapter = GroupAdapter<GroupieViewHolder>()

        view.recyclerview_list.adapter = ChatRecyclerViewAdapter()
        view.recyclerview_list.layoutManager = LinearLayoutManager(activity)

        return view
    } //onCreateView

    inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val userList = ArrayList<LoginUserData>()

        init {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseFirestore.getInstance()
                .collection("loginUserData")
                .document(uid)
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val userdata = task.result?.toObject(LoginUserData::class.java)
                        var studyRoomNumber = userdata?.studyRoomNumber
                        FirebaseFirestore.getInstance()
                            .collection("loginUserData")
                            .whereEqualTo("studyRoomNumber", studyRoomNumber)
                            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                                userList.clear()
                                if(querySnapshot == null) return@addSnapshotListener
                                for (snapshot in querySnapshot?.documents!!) {
                                    if((snapshot.toObject(LoginUserData::class.java)?.uid) != uid) {
                                        userList.add(snapshot.toObject(LoginUserData::class.java)!!)
                                    }
                                }
                                notifyDataSetChanged()
                            }
                    }
                }
        } // init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_list_row, parent, false)
            return CustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val name = holder.itemView.name
            val msg = holder.itemView.msg

            // 화면에 채팅 목록 보여주기
            name.text = userList[position].userName.toString()
            //msg.text = userList[position].uid.toString()
            //msg.text = "마지막 메세지 넣을 자리"

            Log.e(TAG, "position : "+ position+", name.text : "+name.text)

            // 채팅 목록 클릭 시
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("yourUid", userList[position].uid.toString())
                intent.putExtra("yourName", userList[position].userName.toString())
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    } //ChatRecyclerViewAdapter

} //adapter.setOnItemClickListener