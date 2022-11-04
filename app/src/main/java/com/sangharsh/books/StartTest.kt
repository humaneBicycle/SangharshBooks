package com.sangharsh.books

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.downloader.Progress
import com.google.firebase.firestore.FirebaseFirestore
import com.sangharsh.books.model.Test

class StartTest : AppCompatActivity() {
    lateinit var test: Test
    lateinit var pd:ProgressDialog
    lateinit var startTestBtn:Button
    lateinit var currentTestTitle:String
    lateinit var currentTestDescription:String
    lateinit var testTitleTV:TextView
    lateinit var descriptionTV:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_test)
        val testId = intent.getStringExtra("testId")
        testTitleTV = findViewById(R.id.testTitleTV)
        descriptionTV = findViewById(R.id.descriptionTV)
        pd = ProgressDialog(this)
        pd.setTitle("Loading Test")
        pd.setMessage("Please Wait..")
        pd.setCancelable(false)
        pd.show()
        startTestBtn = findViewById(R.id.startTestBtn)
        startTestBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,AttemptTestActivity::class.java)
            startActivity(intent)
        })

        FirebaseFirestore.getInstance().collection("directory").document(testId!!).get().addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result.exists()) {
                    Log.i("abh", "onCreate:on tesetId ${testId} test: ${it.toString()}")
                    test = it.result.toObject(Test::class.java)!!
                    currentTestTitle = test.testTitle.toString()
                    currentTestDescription = test.testDescription.toString()
//                    test.testTitle = test.questions[index].question.toString()
                    Log.i("adi", currentTestTitle)
                    Log.i("adi", currentTestDescription)
                    Log.i("adi", test.noOfQues.toString())
                    Log.i("adi", test.timeAllowed.toString())
                    if (currentTestTitle.isNotEmpty()){
                        testTitleTV.text = currentTestTitle
                    }
                    if (currentTestDescription.isNotEmpty()){
                        descriptionTV.text = currentTestDescription
                    }
                    pd.dismiss()

                }

            }
        }



    //TODO hide loading screen --> done
    // startTest test obj->
    // attemptTest (serializable)

}}