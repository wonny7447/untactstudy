package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.CreateStudyActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.profile_setting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profile_image
import kotlinx.android.synthetic.main.fragment_profile.user_email
import kotlinx.android.synthetic.main.fragment_profile.user_name


class ProfileFragment : Fragment(){

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

        docRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginUserData = task.result?.toObject(LoginUserData::class.java)!!

                    if(loginUserData.onStudy)
                    {
                        mystudy_studyonoff.text = "Study ON"
                    }
                    else
                    {
                        mystudy_studyonoff.text = "Study OFF"
                    }

                }
            } //addonCompleteListener

        var button = view?.findViewById<Button>(R.id.mystudy_profile_edit)
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, profile_setting::class.java)
                startActivity(intent)
            }
        })

        var button2 = view?.findViewById<Button>(R.id.mystudy_studyon)
        button2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mystudy_studyonoff.text = "Study ON"

                docRef.update("onStudy", true) //스터디 여부를 파이어스토어에 전송

            }
        })

        var button3 = view?.findViewById<Button>(R.id.mystudy_studyoff)
        button3?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mystudy_studyonoff.text = "Study OFF"

                docRef.update("onStudy", false) //스터디 여부를 파이어스토어에 전송

            }
        })

        var button4 = view?.findViewById<Button>(R.id.createStudy)
        button4?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, CreateStudyActivity::class.java)
                startActivity(intent)
            }
        })

        return view
    }

    fun changeFragmentTextView(text : String)
    {
        mystudy_studyonoff.text = text
    }

}