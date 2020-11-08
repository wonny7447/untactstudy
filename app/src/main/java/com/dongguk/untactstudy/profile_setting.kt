package com.dongguk.untactstudy

import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.ProfileModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setting.*

class profile_setting : AppCompatActivity() {

    var dataAdapter1: ArrayAdapter<CharSequence>? = null
    var dataAdapter2: ArrayAdapter<CharSequence>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

      var auth : FirebaseAuth? =null
        auth = FirebaseAuth.getInstance()

        val uid = auth.uid

        val newname = newname.text.toString()
        val newintrduction = newintroduction.text.toString()

        val profilesetting = ProfileModel(newname, newintrduction)

        val firestore = FirebaseFirestore? = null
        firestore= FirebaseFirestore.getInstance().collection("profile_setting")
            firestore.document(uid)
            firestore.set(profilesetting)
    }
}