package com.dongguk.untactstudy

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dongguk.untactstudy.Model.addpostModel
import com.google.firebase.storage.FirebaseStorage
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

        postdetail_title.text = title
        postdetail_body.text = body
        attachFileName.text = postphotourl
        postdetail_time.text = "게시일자 : "+time.toString()
        postdetail_name.text = "게시자 : "+userName
        Glide.with(this).load(userImage).into(post_detail_user_image)

        attach_download.setOnClickListener{
            val extender = postphotourl.substringAfterLast(".")

            Log.e(TAG,"button click : "+postphotourl+"extender : "+extender)

            var storageRef = FirebaseStorage.getInstance().reference.child("files").child(postphotourl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                DownloadFileFromURL(extender).execute(uri.toString())
                Toast.makeText(this, "첨부파일 다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

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