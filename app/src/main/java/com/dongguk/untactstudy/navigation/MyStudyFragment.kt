package com.dongguk.untactstudy.navigation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.*
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.addpostModel
import com.dongguk.untactstudy.chat.ChatRoomActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.android.synthetic.main.activity_addpost.view.*
import kotlinx.android.synthetic.main.fragment_chatting.view.*
import kotlinx.android.synthetic.main.fragment_mystudy.*
import kotlinx.android.synthetic.main.fragment_mystudy.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.post_list_row.view.*
import java.text.SimpleDateFormat

class MyStudyFragment : Fragment() {

    private val TAG = LoginActivity::class.java.simpleName

    var studyRoomNumber: String = "0"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_mystudy, container, false)

        ViewCompat.setAccessibilityPaneTitle(view, "스터디 룸")

        var loginUserData = LoginUserData()
        val db = FirebaseFirestore.getInstance()
                .collection("loginUserData")
                .document(FirebaseAuth.getInstance()?.currentUser!!.uid)


        var member_list = view?.findViewById<Button>(R.id.member_list)
        member_list?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var myStudyRoomNumber: String
                db.get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var userdata = task.result?.toObject(LoginUserData::class.java)
                            myStudyRoomNumber = userdata?.studyRoomNumber.toString()

                            val intent = Intent(context, StudyMemberListActivity::class.java)
                            intent.putExtra("myStudyRoomNumber", myStudyRoomNumber)
                            startActivity(intent)
                        }
                    }
            }

        }) //memberlist로 이동

        var add_button = view?.findViewById<Button>(R.id.add_post)
        add_button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var myStudyRoomNumber: String
                db.get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var userdata = task.result?.toObject(LoginUserData::class.java)
                            myStudyRoomNumber = userdata?.studyRoomNumber.toString()

                            val intent = Intent(context, addpost::class.java)
                            intent.putExtra("myStudyRoomNumber", myStudyRoomNumber)
                            intent.putExtra("userName", userdata?.userName.toString())
                            intent.putExtra("userUid", userdata?.uid.toString())
                            intent.putExtra("userImage", userdata?.userPhotoUrl.toString())
                            startActivity(intent)
                        }
                    }
            }
        }) // add 버튼 누르면 로그인데이터 가져가면서 intent

        var studyOnButton = view?.findViewById<Button>(R.id.mystudy_studyon)
        studyOnButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                db.update("onStudy", true)
                        .addOnSuccessListener {
                            Log.e(TAG, "Study On으로 상태 변경 성공")
                        }
            }

        }) //studyOnButton 클릭 이벤트 이동

        var studyOffButton = view?.findViewById<Button>(R.id.mystudy_studyoff)
        studyOffButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                db.update("onStudy", false)
                        .addOnSuccessListener {
                            Log.e(TAG, "Study Off으로 상태 변경 성공")
                        }
            }

        }) //studyOffButton 클릭 이벤트 이동

        var score_button = view?.findViewById<Button>(R.id.mystudy_myscore)
        score_button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, ScoreActivity::class.java)
                startActivity(intent)
            }
        })

        view.postlistRecyclerView.adapter = postRecyclerViewAdapter()
        view.postlistRecyclerView.layoutManager = LinearLayoutManager(activity)


        return view
    }

    inner class postRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val postList = ArrayList<addpostModel>()

        init {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            var myStudyRoomNumber: String

            FirebaseFirestore.getInstance()
                    .collection("loginUserData")
                    .document(uid)
                    .get()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            val userData = task.result?.toObject(LoginUserData::class.java)
                            myStudyRoomNumber = userData?.studyRoomNumber.toString()
                            Log.e(TAG, " db myStudyRoomNumber : " + myStudyRoomNumber)

                            FirebaseFirestore.getInstance()
                                .collection("postData")
                                .document(myStudyRoomNumber)
                                .collection("studyPostData")
                                .orderBy("time", Query.Direction.DESCENDING)
                                .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                                    postList.clear()
                                    if(querySnapshot == null) return@addSnapshotListener
                                    for (snapshot in querySnapshot?.documents!!) {
                                        postList.add(snapshot.toObject(addpostModel::class.java)!!)
                                    }
                                    notifyDataSetChanged()
                                }
                        }
                    } //addOnCompleteListener
        } // init

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_list_row, parent, false)
            return postViewHolder(view)
        } //onCreateViewHolder

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if(postList[position].userImage == "" || postList[position].userImage == null) {
                Glide.with(this@MyStudyFragment).load(R.drawable.profileimage).into(holder.itemView.post_list_user_image)
            } else {
                Glide.with(this@MyStudyFragment).load(postList[position].userImage).into(holder.itemView.post_list_user_image)
            }

            holder.itemView.post_list_title.setSingleLine()
            holder.itemView.post_list_title.text = postList[position].title
            holder.itemView.post_list_body.setSingleLine()
            holder.itemView.post_list_body.text = postList[position].body
            if(postList[position].postphotourl.toString() != "") {
                holder.itemView.post_list_uri.setImageResource(android.R.drawable.ic_menu_save)
            }

            val postTime = SimpleDateFormat("yyyy-MM-dd").format(postList[position].time)
            holder.itemView.post_list_time.text = postTime

            holder.itemView.setOnClickListener {
                val intent = Intent(context, postdetail::class.java)
                intent.putExtra("title", postList[position].title)
                intent.putExtra("body", postList[position].body)
                intent.putExtra("postphotourl", postList[position].postphotourl)
                intent.putExtra("time", postTime)
                intent.putExtra("userName", postList[position].userName)
                intent.putExtra("userUid", postList[position].userUid)
                intent.putExtra("studyRoomNumber", postList[position].studyRoomNumber)
                intent.putExtra("userImage", postList[position].userImage)
                startActivity(intent)
            }
            Log.e(TAG, "position : " + position)
            Log.e(TAG, "holder--- title.text : " + holder.itemView.post_list_title.text + ", body.text : " + holder.itemView.post_list_body.text)
            Log.e(TAG, "default--- title.text : " + postList[position].title + ", body.text : " + postList[position].body)
        }

        override fun getItemCount(): Int {
            return postList.size
        }
        inner class postViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }
}


















