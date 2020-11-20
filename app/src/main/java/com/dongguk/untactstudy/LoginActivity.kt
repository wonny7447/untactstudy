
package com.dongguk.untactstudy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.navigation.ProfileFragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    //로그 변수
    private val TAG = LoginActivity::class.java.simpleName

    // firebase 인증을 위한 변수
    var auth : FirebaseAuth ? = null

    // 구글 로그인 연동에 필요한 변수
    var googleSignInClient : GoogleSignInClient ? = null
    var GOOGLE_LOGIN_CODE = 9001

    // onCreate는 Acitivity가 처음 실행 되는 상태에 제일 먼저 호출되는 메소드로 여기에 실행시 필요한 각종 초기화 작업을 적어줌
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 페이지가 첫화면임 (activity_main.xml의 레이아웃 사용)
        setContentView(R.layout.activity_login)

        // firebaseauth를 사용하기 위한 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // xml에서 구글 로그인 버튼 코드 가져오기
        var google_sign_in_button = findViewById<SignInButton>(R.id.google_sign_in_button)

        // 구글 로그인 버튼 클릭 시 이벤트 : googleLogin function 실행
        google_sign_in_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient?.signOut()
            googleLogin()
        }

        // 구글 로그인을 위해 구성되어야 하는 코드 (Id, Email request)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    } // onCreate

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    } // googleLogin

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE) {
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                if(result.isSuccess) {
                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        } //if
    } // onActivityResult

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        // 로그인 성공 시
                        val isNew = task.result!!.additionalUserInfo!!.isNewUser

                        // 화면 이동
                        if(isNew){  //최초 로그인 유저인 경우

                            // FireStore 데이터베이스에 로그인 사용자 정보 저장 (테이블 이름 : loginUserData)
                            val uid = auth?.uid.toString()
                            val userName = auth?.currentUser?.displayName.toString()
                            val email = auth?.currentUser?.email.toString()
                            val photoUrl = auth?.currentUser?.photoUrl.toString()
                            val time = System.currentTimeMillis()
                            val loginUserData = LoginUserData(uid, userName, email, photoUrl, time, "", "", false)

                            val db = FirebaseFirestore.getInstance().collection("loginUserData")
                            db.document(uid)
                                .set(loginUserData)
                                .addOnCompleteListener {
                                    Log.e(TAG, "DB에 로그인 정보 생성 성공")
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, "DB에 로그인 정보 생성 실패")
                                }

                            startActivity(Intent(this, StudyCategoryActivity::class.java))  //관심 스터디 분야창으로 이동
                            finish()    //로그인 화면 종료
                        }
                        else{   //기존 로그인 유저인 경우

                            val time = System.currentTimeMillis()
                            val db = FirebaseFirestore.getInstance().collection("loginUserData")
                            db.document(auth?.uid.toString())
                                .update("time", time)
                                .addOnCompleteListener {
                                    Log.e(TAG, "DB에 로그인 시간 갱신 성공")
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, "DB에 로그인 시간 갱신 실패")
                                }

                            startActivity(Intent(this, MainActivity::class.java)) //메인 액티비티로 이동
                        }
                    } else {
                        // 로그인 실패 시
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    } //firebaseAuthWithGoogle
}