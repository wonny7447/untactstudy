package com.dongguk.untactstudy.navigation

import android.app.AlertDialog
import android.content.Intent
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
import com.dongguk.untactstudy.Model.*
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.ShowDetail
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

    val studyRoomList = ArrayList<StudyModel>()

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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                val studyNameData = studyData!!.studyName.toLowerCase()
                                val studyRoomNumberData = studyData!!.studyRoomNumber.toString()
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if (studyNameData.contains(query?.toLowerCase().toString()) || studyRoomNumberData == query && studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                            for (snapshot in querySnapshot?.documents!!) {
                                val studyData = snapshot.toObject(StudyModel::class.java)
                                var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                if(studyStartDate > today)
                                    studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                        for (snapshot in querySnapshot?.documents!!) {
                            val studyData = snapshot.toObject(StudyModel::class.java)
                            var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                            if(studyStartDate > today)
                                studyRoomList.add(snapshot.toObject(StudyModel::class.java)!!)
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

            studyName.text = studyRoomList[position].studyName
            studyInformation.text = studyRoomList[position].studyInfo


            var studyRoomNum = studyRoomList[position].studyRoomNumber.toString()

            studyRoom = studyRoomList[position].studyRoomNumber.toString()

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ShowDetail::class.java)
                //putExtra
                intent.putExtra("transfer",studyRoom)
                Log.e(TAG, "전달값:"+studyRoom)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return studyRoomList.size
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}

