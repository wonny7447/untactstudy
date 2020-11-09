package com.dongguk.untactstudy.navigation
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.dongguk.untactstudy.CreateStudyActivity
import com.dongguk.untactstudy.MainActivity
import com.dongguk.untactstudy.R
import com.dongguk.untactstudy.profile_setting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import android.content.Intent as s

class ProfileFragment : Fragment(){
/*
    var auth: FirebaseAuth? = null
    var firestore : FirebaseFirestore?=null

    var uid : String?=null
    var useremail : String? = null*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_profile, container, false)
/*
        auth = FirebaseAuth.getInstance()
        val uid = auth?.uid

        firestore = FirebaseFirestore.getInstance()
*/
        var button = view?.findViewById<Button>(R.id.mystudy_profile_edit)
        button?.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, profile_setting::class.java)
                startActivity(intent)
            }
        })

        var button2 = view?.findViewById<Button>(R.id.createStudy)
        button2?.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, CreateStudyActivity::class.java)
                startActivity(intent)
            }
        })

        return view
    }
}