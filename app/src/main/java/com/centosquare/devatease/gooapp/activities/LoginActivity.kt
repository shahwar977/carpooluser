package com.centosquare.devatease.gooapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.centosquare.devatease.gooapp.R
import com.centosquare.devatease.gooapp.utils.Constants
import com.centosquare.devatease.gooapp.utils.SharedManager
import kotlinx.android.synthetic.main.activity_pinview.*

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin:Button
    private lateinit var mobileNoEditText:EditText
    private lateinit var mobileNo:String
    private lateinit var userPreference:SharedManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login  )

        viewComponents()
        userPreference = SharedManager(this)
        btnLogin.setOnClickListener {

            mobileNo = mobileNoEditText.text.toString().trim()

            if (!mobileNo.isEmpty() && mobileNo.length == 11) {


                userPreference.setStr(Constants.userNamePreference,mobileNo)

                val intent = Intent(this,PinviewActivity::class.java)
                intent.putExtra("MOBILENUMBER", mobileNo)
                startActivity(intent)
                finish()


            }
            else{

                mobileNoEditText.setError("Please enter a valid phone number")
                mobileNoEditText.requestFocus()

            }

        }



    }

    fun viewComponents(){

        mobileNoEditText = findViewById(R.id.editText_phon_no_login)
        btnLogin = findViewById(R.id.btn_login)


    }


}
