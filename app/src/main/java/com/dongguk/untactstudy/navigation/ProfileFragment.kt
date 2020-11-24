package com.dongguk.untactstudy.navigation

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.*
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.TodoData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.fragment_chatting.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profile_image
import kotlinx.android.synthetic.main.fragment_profile.user_email
import kotlinx.android.synthetic.main.fragment_profile.user_name
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment(){

    // Log
    private val TAG = LoginActivity::class.java.simpleName
    var googleSignInClient : GoogleSignInClient? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = LayoutInflater.from(activity).inflate( R.layout.fragment_profile, container, false )

        var userdata : LoginUserData ? = null
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore.getInstance().collection("loginUserData")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        userdata = task.result?.toObject(LoginUserData::class.java)

                        user_email?.text = userdata?.userEmail.toString()
                        user_name?.text = userdata?.userName.toString()
                        Glide.with(this).load(userdata?.userPhotoUrl).into(profile_image)
                        if(userdata?.introduction.toString() == "") {
                            user_infom?.text = "프로필을 등록하세요"
                        } else {
                            user_infom?.text = userdata?.introduction.toString()
                        }

                    }
                }


        var loginUserData = LoginUserData()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("loginUserData").document(uid)

        var button = view?.findViewById<Button>(R.id.mystudy_profile_edit)
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, profile_setting::class.java)
                startActivity(intent)
            }
        })

        // 로그아웃 버튼 클릭 시
        var logout = view?.findViewById<Button>(R.id.logout)
        logout?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                FirebaseAuth.getInstance().signOut()
                googleSignInClient?.signOut()

                var logoutIntent = Intent (context, LoginActivity::class.java)
                logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(logoutIntent)
            }
        })

        var button4 = view?.findViewById<Button>(R.id.createStudy)
        button4?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var studyRoomNumber : String
                var leader : Boolean

                FirebaseFirestore.getInstance()
                    .collection("loginUserData")
                    .document(uid)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userData = task.result?.toObject(LoginUserData::class.java)
                            studyRoomNumber = userData?.studyRoomNumber.toString()
                            leader = userData?.leader!!

                            // 조건1) 가입된 스터디 없으므로 스터디 생성 가능
                            if(studyRoomNumber == "0") {
                                val intent = Intent(context, CreateStudyActivity::class.java)
                                startActivity(intent)
                            }
                            // 조건2) 가입한 스터디가 있는 경우
                            else if(studyRoomNumber != "0") {
                                // 가입한 스터디 정보를 먼저 불러옴
                                FirebaseFirestore.getInstance()
                                    .collection("studyInfo")
                                    .document(studyRoomNumber)
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful) {
                                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                                            val studyData = task.result?.toObject(StudyModel::class.java)
                                            var studyStartDate : Long = sdf.parse(studyData?.studyStartDate).time
                                            var studyEndDate = sdf.parse(studyData?.studyEndDate).time
                                            var today = sdf.parse(SimpleDateFormat("yyyy-MM-dd").format(Date())).time
                                            Log.e(TAG, "studyRoomNumber : "+studyRoomNumber+", studyStartDate : "+studyStartDate+", studyEndDate : "+studyEndDate+", today : "+today)

                                            // 조건 2-1) 종료일자가 지났으면 스터디 생성 가능
                                            if(studyEndDate < today) {
                                                val intent = Intent(context, CreateStudyActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                noticeAndSwitch()
                                            }
                                        } //if(task.isSuccessful)
                                    } //addOnCompleteListener
                            }
                        }
                    }
            }
        })

        return view
    }

    fun signOut() {
        // Google Logout
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }
    }

    // 파라미터에 따라서 알럿을 보여주고 화면 전환 시키는 function
    // 화면전환은 fragment -> fragment만 가능
    fun noticeAndSwitch() {
        var builder = AlertDialog.Builder(context)
        builder.setTitle("")
        builder.setMessage("참여 중인 스터디가 있어서 신규 스터디 생성이 불가합니다.")

        var listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        fragmentManager?.beginTransaction()?.remove(ProfileFragment())?.commit()
                        fragmentManager?.beginTransaction()?.replace(R.id.main_content, MyStudyFragment())?.commit()
                    }
                }
            }
        }
        builder.setPositiveButton("확인", listener)
        builder.show()
    } //noticeAndSwitch
}