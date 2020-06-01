package com.centosquare.devatease.gooapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.utils.SharedManager
import com.chaos.view.PinView
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PinviewActivity : AppCompatActivity() {

    private lateinit var btnDone: Button
    private var pinview: PinView? = null
    private var verificationid: String? = null
    private var auth: FirebaseAuth? = null
    private lateinit var mobileNumber:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinview)

        viewComponents()


        if (mobileNumber != null) {
            sendverificationcode("+92$mobileNumber")
        }


        pinview!!.setOnEditorActionListener { textView, i, keyEvent ->
            var handeled = false
            if (i == EditorInfo.IME_ACTION_DONE) {
                val code = pinview!!.getText()!!.toString().trim()
                if (code.isEmpty() || code.length < 6) {

                    Toast.makeText(this@PinviewActivity, "Enter code...", Toast.LENGTH_SHORT).show()
                }

                else {
                    verifycode(code)

                    handeled = true
                }
            }

            handeled
        }

        btnDone.setOnClickListener {

            val code = pinview!!.getText()!!.toString().trim()
            if (code.isEmpty() || code.length < 6) {

                Toast.makeText(this@PinviewActivity, "Enter code...", Toast.LENGTH_SHORT).show()
            }


            else {
                verifycode(code)
            }

        }
    }

    fun viewComponents(){

        mobileNumber = intent.getStringExtra("MOBILENUMBER")
        auth = FirebaseAuth.getInstance()
        pinview = findViewById(R.id.pinview)
        btnDone = findViewById(R.id.btn_pinview_done)


    }

    private fun sendverificationcode(mobilenumber: String) {
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(mobilenumber, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callback)
    }


    private val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)

            verificationid = p0
        }



        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            val code = phoneAuthCredential.smsCode
            if (code != null) {
                pinview!!.setText(code)
                verifycode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {

            Log.i("verification", "on verification failed" + e.message)

            Toast.makeText(this@PinviewActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifycode(code: String) {
        try {

            val credential = PhoneAuthProvider.getCredential(verificationid!!, code)
            signinwithcredential(credential)

        } catch (e: Exception) {

            val toast = Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }

    }

    private fun signinwithcredential(crenditial: PhoneAuthCredential) {

        auth!!.signInWithCredential(crenditial).addOnCompleteListener { task ->

            if (task.isSuccessful) {


                startActivity(Intent(this@PinviewActivity,UserInfoActivity::class.java))

            } else {

                val userPreference = SharedManager(this)
                userPreference.removeFromPrefs()

                Log.i("credentials", "credentials" + task.exception!!.message)

                Toast.makeText(this@PinviewActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        val userPreference = SharedManager(this)
        userPreference.removeFromPrefs()

        finish()
    }


}
