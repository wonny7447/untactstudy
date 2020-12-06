package com.dongguk.untactstudy

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.Adapter.PostCommentItem
import com.dongguk.untactstudy.Model.PostCommentModel
import com.dongguk.untactstudy.Model.addpostModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_postdetail.*
import kotlinx.android.synthetic.main.post_list_row.view.*
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class postdetail : AppCompatActivity() {

    // log
    private val TAG = LoginActivity::class.java.simpleName

    // RealTime Database
    val database = FirebaseDatabase.getInstance()
    val postCommentSetDB = database.getReference("postComment")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postdetail)

        val title = intent.getStringExtra("title").toString()
        val body = intent.getStringExtra("body").toString()
        val postphotourl = intent.getStringExtra("postphotourl").toString()
        val time = intent.getStringExtra("time").toString()
        val userName = intent.getStringExtra("userName").toString()
        val userImage = intent.getStringExtra("userImage").toString()
        val userUid = intent.getStringExtra("userUid").toString()
        val studyRoomNumber = intent.getStringExtra("studyRoomNumber").toString()
        val postDocumentId = intent.getStringExtra("postDocumentId").toString()

        postdetail_title.text = title
        postdetail_body.text = body
        attachFileName.text = postphotourl
        postdetail_time.text = "게시일자 : "+time.toString()
        postdetail_name.text = "게시자 : "+userName
        Glide.with(this).load(userImage).into(post_detail_user_image)


        // 첨부파일 다운로드 클릭 버튼 이벤트
        attach_download.setOnClickListener{
            val extender = postphotourl.substringAfterLast(".")

            Log.e(TAG,"button click : "+postphotourl+"extender : "+extender)

            var storageRef = FirebaseStorage.getInstance().reference.child("files").child(postphotourl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                DownloadFileFromURL(extender).execute(uri.toString())
                Toast.makeText(this, "첨부파일 다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
            }
        } // attach_download

        // 댓글 전송 버튼 클릭 시 이벤트
        send_button.setOnClickListener {
            var commentText = post_comment_text.text.toString()

            val postCommentModel = PostCommentModel(
                    SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()),
                    userName,
                    userUid,
                    commentText,
                    postDocumentId
            )
            postCommentSetDB.child(postDocumentId).push().setValue(postCommentModel)
            post_comment_text.setText("")
        } //send_button

        // 댓글 리사이클러뷰 처리
        var adapter = GroupAdapter<GroupieViewHolder>()
        val postCommentReadDB = database.getReference("postComment").child(postDocumentId)

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 방금 전송한 메세지를 로그에 찍음
                Log.d(TAG,"p0 : "+snapshot)

                val model = snapshot.getValue(PostCommentModel::class.java)

                val comment = model?.comment.toString()
                val userName = model?.userName.toString()
                /*val userUid = model?.userUid
                val postDocumentId = model?.postDocumentId
                val time = model?.time*/

                adapter.add(PostCommentItem(userName, comment))

            } //onChildAdded
        } //childEventListener

        postCommentRecyclerView.adapter = adapter
        postCommentReadDB.addChildEventListener(childEventListener)
    }

    // 첨부파일 다운로드
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