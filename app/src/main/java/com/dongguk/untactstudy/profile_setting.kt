package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.ProfileModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setting.*

class profile_setting : AppCompatActivity() {

   var firestore : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        var btn = findViewById(R.id.mystudy_insert) as Button

      btn.setOnClickListener(object : View.OnClickListener {
          override fun onClick(v: View?) {


              val newname = new_name.text.toString()
              val newintrduction = new_introduction.text.toString()

              val profilesetting = ProfileModel(newname, newintrduction)

              val store = FirebaseFirestore.getInstance().collection("profile_setting")
                      .document("name")
                      .set(profilesetting)
                      .addOnCompleteListener {
                          println("저장")
              startActivity(Intent(this@profile_setting, MainActivity::class.java))
                      }
          }
      }
      )

    }
}