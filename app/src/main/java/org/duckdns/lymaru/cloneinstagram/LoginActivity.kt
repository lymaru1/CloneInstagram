package org.duckdns.lymaru.cloneinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.duckdns.lymaru.cloneinstagram.databinding.ActivityLoginBinding

 class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    val GOOGLE_LOGIN_CODE : Int = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // data binging 할당
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        // firebaseAuth 할당
        auth = FirebaseAuth.getInstance()

        binding.emailLoginButton.setOnClickListener {
            // 가입 및 로그인
            signInAndSignUp()
        }
        binding.googleSignInButton.setOnClickListener {
            // 구글 계정으로 로그인
            googleLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
    }
    fun googleLogin(){
        var signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if(result!!.isSuccess){
                var account = result?.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    // Login
                    moveMainPage(task.result?.user)
                } else {
                    // Showw the Error Message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signInAndSignUp(){
        auth.createUserWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
            .addOnCompleteListener {
                task ->
                if(task.isSuccessful) {
                    // Creating a user account
                    moveMainPage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()){
                    // Show the Error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }else{
                    // Login if you have account
                    signInEmail()
                }
       }
    }
    fun signInEmail(){
        auth.signInWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
            .addOnCompleteListener {
             task ->
            if (task.isSuccessful) {
                 // Login
                moveMainPage(task.result?.user)
            } else {
                 // Show the Error Message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    fun moveMainPage(user:FirebaseUser?){
        if (user != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}