package com.dongguk.untactstudy

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.dongguk.untactstudy.Adapter.ChatAttachLeftAndRight
import com.dongguk.untactstudy.Model.addpostModel
import com.dongguk.untactstudy.navigation.FindStudyFragment
import com.dongguk.untactstudy.navigation.MyStudyFragment
import com.dongguk.untactstudy.navigation.TodolistFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_addpost.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.activity_study_member_list.*
import kotlinx.android.synthetic.main.fragment_mystudy.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class addpost : AppCompatActivity() {

    // File 전송에 쓰이는 코드
    var FILE = 1
    var postphotouri : String ?= ""

    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)
        setTitle("새 글 쓰기")
        postattach_button.setImageResource(android.R.drawable.ic_menu_save)

        val userUid = intent.getStringExtra("userUid").toString()
        val userName = intent.getStringExtra("userName").toString()
        val myStudyRoomNumber = intent.getStringExtra("myStudyRoomNumber").toString()
        val userImage = intent.getStringExtra("userImage").toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        postattach_button.setOnClickListener {
            openContent()
        }

        addpost_insert.setOnClickListener {
            val title = post_title.text.toString()
            val body = post_body.text.toString()
            val time = System.currentTimeMillis()
            val postphotouri =  postphotouri.toString()
            var postDocumentId = userUid+"-"+time.toString()
            val addpostModel = addpostModel(
                    title,
                    body,
                    postphotouri,
                    time,
                    userName,
                    userUid,
                    myStudyRoomNumber,
                    userImage,
                    postDocumentId
            )

            FirebaseFirestore.getInstance()
                .collection("postData")
                .document(myStudyRoomNumber)
                .collection("studyPostData")
                .document(postDocumentId)
                .set(addpostModel)
                .addOnCompleteListener {
                    Log.e(TAG, "게시글 저장 완료")
                    finish()
                    supportFragmentManager.beginTransaction().replace(R.id.addPostActivity, MyStudyFragment()).addToBackStack(null).commit()
                }
                .addOnFailureListener {
                    Log.e(TAG, "게시글 저장 실패")
                }
        }
    }

    fun openContent() {
        var intent = Intent(Intent.ACTION_PICK)
        //intent.type = auth.uid.toString()+"/*"
        intent.type = "*/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, FILE)
    } //openContent

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == FILE) {
            var attachUri = data?.data!!
            var attachUriString = data?.data.toString()
            uploadFile(attachUri, attachUriString)
        }
    } //onActivityResult

    fun uploadFile(attachUri:Uri, attachUriString: String) {

        var metaCursor = contentResolver.query(attachUri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)!!
        metaCursor.moveToFirst()
        var fileName = metaCursor.getString(0)
        metaCursor.close()

        var storageRef = FirebaseStorage.getInstance().reference.child("files").child(fileName)

        storageRef.putFile(attachUri).addOnSuccessListener {
            Log.e(TAG, "storage success")
            postphotouri = fileName
            Log.e(TAG, "postphotouri : "+postphotouri)
            attachFileName.text = postphotouri
        }
    } //uploadFile
}
