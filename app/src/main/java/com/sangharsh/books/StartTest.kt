package com.sangharsh.books

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sangharsh.books.model.Test

class StartTest : AppCompatActivity() {
    lateinit var test: Test
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_test)

        val testId = intent.getStringExtra("testId")
        FirebaseFirestore.getInstance().collection("directory").document(testId!!).get().addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result.exists()) {
                    Log.i("abh", "onCreate:on tesetId ${testId} test: ${it.toString()}")
                    test = it.result.toObject(Test::class.java)!!

                }

            }
        }



        //TODO hide loading screen --> startTest test obj-> attemptTest (serializable)





    }
}