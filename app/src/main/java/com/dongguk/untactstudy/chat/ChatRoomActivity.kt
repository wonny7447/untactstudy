package com.dongguk.untactstudy.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.Adapter.*
import com.dongguk.untactstudy.LoginActivity
import com.dongguk.untactstudy.Model.ChatModel
import com.dongguk.untactstudy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.chat_attach_right_me.*
import kotlinx.android.synthetic.main.chat_attach_right_me.view.*
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity() {

    // FireStore
    val db = FirebaseFirestore.getInstance()

    // RealTime Database
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("message")
    val myRef_list = database.getReference("message-user-list")

    // log
    private val TAG = LoginActivity::class.java.simpleName

    // File 전송에 쓰이는 코드
    var GALLERY = 0
    var FILE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // ChatListActivity.kt에서 intent.putextra로 보낸 값 (대화방의 user name, uid)
        val yourUid = intent.getStringExtra("yourUid").toString()
        val yourName = intent.getStringExtra("yourName").toString()
        setTitle(yourName+"님과의 채팅")

        // 나의 uid 값
        val myUid = FirebaseAuth.getInstance()?.currentUser!!.uid

        Log.e(TAG, "myUid : "+myUid+", yourUid : "+yourUid+", yourName : "+yourName)

        chat_edittext.requestFocus()
        val keyboard : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        var adapter = GroupAdapter<GroupieViewHolder>()

        // RealTime DB에서 message 테이블 값 가져오기
        val readRef = database.getReference("message").child(myUid).child(yourUid)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        // 메세지 전송 버튼 클릭 시 이벤트
        send_button.setOnClickListener {
            sendMessage(myUid, yourUid, yourName, "", "")
        } //send_button.setOnClickListener

        // 파일전송 클릭 시 이벤트
        attach_button.setOnClickListener {
            openContent(yourUid, yourName)
        }

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 방금 전송한 메세지를 로그에 찍음
                Log.d(TAG,"p0 : "+snapshot)

                val model = snapshot.getValue(ChatModel::class.java)
                val msg = model?.message.toString()
                val who = model?.who
                val uri = model?.photoUri.toString()
                val fileName = model?.fileName.toString()

                if(who == "me") {
                    if(fileName == "") {
                        adapter.add(ChatRightMe(msg))
                    } else {
                        adapter.add(ChatAttachLeftAndRight(fileName, "", "R"))
                    }
                } else {
                    if(fileName == "") {
                        adapter.add(ChatLeftYou(msg, yourName))
                    } else {
                        adapter.add(ChatAttachLeftAndRight(fileName, yourName, "L"))
                    }
                }
            } //onChildAdded
        } //childEventListener

        recyclerView_chat.adapter = adapter
        readRef.addChildEventListener(childEventListener)


        // 첨부파일을 보낸 챗을 클릭하면 첨부파일이 다운로드 됨
        adapter.setOnItemClickListener { item, view ->
            val fileName = (item as ChatAttachLeftAndRight).fileName
            val extender = fileName.substringAfterLast(".")

            Log.e(TAG,"button click : "+fileName+"extender : "+extender)

            var storageRef = FirebaseStorage.getInstance().reference.child("files").child(fileName)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                DownloadFileFromURL(extender).execute(uri.toString())
                Toast.makeText(this, "첨부파일 다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
            }
        }

    }//onCreate

    fun sendMessage(myUid: String, yourUid: String, yourName: String, uri: String, fileName: String) {
        Log.e(TAG,"5. sendMessage : "+myUid+", yourUid : "+yourUid+", yourName : "+yourName)

        var message : String ? = null

        if(fileName == "") {
            message = chat_edittext.text.toString()
        } else {
            message = "파일 전송"
        }
        chat_edittext.setText("")

        // 메세지를 보내는 사람
        val chat = ChatModel(
                myUid,
                yourUid,
                message,
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()),
                "me",
                yourName,
                uri,
                fileName
        )
        myRef.child(myUid).child(yourUid).push().setValue(chat)

        // 메세지를 받는 사람
        val chat_get = ChatModel(
                yourUid,
                myUid,
                message,
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()),
                "you",
                yourName,
                uri,
                fileName
        )
        myRef.child(yourUid).child(myUid).push().setValue(chat_get)

        // 마지막으로 보내는 메세지 업데이트 용
        myRef_list.child(myUid).child(yourUid).setValue(chat)

        chat_edittext.setText("")
    }

    fun openContent(yourUid : String, yourName : String) {
        var intent = Intent(Intent.ACTION_PICK)
        //intent.type = auth.uid.toString()+"/*"
        intent.type = "*/*"
        intent.setAction(Intent.ACTION_GET_CONTENT)
        intent.putExtra("yourUid", yourUid)
        intent.putExtra("yourName", yourName)
        startActivityForResult(intent, FILE)
    } //openContent

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val yourUid = intent.getStringExtra("yourUid").toString()
        val yourName = intent.getStringExtra("yourName").toString()
        val myUid = FirebaseAuth.getInstance()?.currentUser!!.uid

        if(requestCode == FILE) {
            var attachUri = data?.data!!
            var attachUriString = data?.data.toString()
            uploadFile(myUid, yourUid, yourName, attachUri, attachUriString)
        }
    } //onActivityResult


    fun uploadFile(myUid : String, yourUid : String, yourName : String, attachUri:Uri, attachUriString: String) {

        var metaCursor = contentResolver.query(attachUri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)!!
        metaCursor.moveToFirst()
        var fileName = metaCursor.getString(0)
        metaCursor.close()

        var storageRef = FirebaseStorage.getInstance().reference.child("files").child(fileName)

        storageRef.putFile(attachUri).addOnSuccessListener {
            Log.e(TAG, "storage success")
            sendMessage(myUid, yourUid, yourName, attachUriString, fileName)
        }
    } //uploadFile

    // image 업로드용 function :: 미사용
    fun uploadPhoto(myUid : String, yourUid : String, yourName : String, attachUri:Uri, attachUriString: String) {

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var fileName = "Attach_"+timestamp+".png"

        var storageRef = FirebaseStorage.getInstance().reference.child("images").child(fileName)

        storageRef.putFile(attachUri).addOnSuccessListener {
            Log.e(TAG, "storage success")
            sendMessage(myUid, yourUid, yourName, attachUriString, "")
        }
    } //uploadPhoto

    inner class DownloadFileFromURL(extender:String) : AsyncTask<String?, String?, String?>() {
        val innerExtender = extender
        override fun doInBackground(vararg p0: String?): String? {

            // file download path
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

            //image download url
            val url = URL(p0[0])
            val conection = url.openConnection()
            conection.connect()

            // input stream to read file - with 8k buffer
            val input = BufferedInputStream(url.openStream(), 8192)

            // output stream to write file
            var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val output = FileOutputStream(downloadFolder + "/ATTACH"+timestamp+"."+innerExtender)
            val data = ByteArray(1024)
            var total = 0L

            // writing data to file
            var count : Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()

                output.write(data, 0, count)
            }
            // flushing output
            output.flush()
            // closing streams
            output.close()
            input.close()

            return null
        } //doInBackground

    } //DownloadFileFromURL
}