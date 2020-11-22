package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.*
import com.dongguk.untactstudy.Adapter.Recycler
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.TodoData
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
import kotlinx.android.synthetic.main.fragment_findstudy.view.*
import kotlinx.android.synthetic.main.message_list_row.view.*


class FindStudyFragment : Fragment(){

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_findstudy, container, false)
        var adapter = GroupAdapter<GroupieViewHolder>()

        view.my_recycler_view.adapter = StudyRecyclerViewAdapter()
        view.my_recycler_view.layoutManager = LinearLayoutManager(activity)

        return view
    } //onCreateView

    inner class StudyRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val studyList = ArrayList<StudyModel>()
        //val loginUserData :

        init {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseFirestore.getInstance()
                .collection("studyInfo")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    studyList.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {
                        studyList.add(snapshot.toObject(StudyModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        } // init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_list_row, parent, false)

            return CustomViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var name = holder.itemView.name
            var studyRoomNum = holder.itemView.msg
            var studyRoom : String

            name.text = studyList[position].studyName
            studyRoomNum.text = studyList[position].studyRoomNumber.toString()
            studyRoom = studyList[position].studyRoomNumber.toString()


            // 가입 버튼을 눌렀다고 가정
            holder.itemView.setOnClickListener {
                Log.e(TAG, "studyRoomNum : "+studyRoomNum.text)

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
                                    if(querySnapshot == null) return@addSnapshotListener
                                    for (snapshot in querySnapshot?.documents!!) {
                                        var TodoData = snapshot.toObject(TodoData::class.java)!!.list

                                        for(i in 0 .. ((TodoData.size) - 1)) {
                                            var orientText : String = TodoData[i].toString()
                                            TodoData.set(i, "F"+orientText)
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
        }

        override fun getItemCount(): Int {
            return studyList.size
        }

        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    } //StudyRecyclerViewAdapter

} //adapter.setOnItemClickListener