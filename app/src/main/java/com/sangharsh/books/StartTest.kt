package com.sangharsh.books

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sangharsh.books.model.Question
import com.sangharsh.books.model.Test

class StartTest : AppCompatActivity() {
    lateinit var test: Test
    lateinit var pd:ProgressDialog
    lateinit var startTestBtn:Button
    lateinit var currentTestTitle:String
    lateinit var currentTestDescription:String
     var noOfQues:Int =0
    lateinit var timeAllowed:String
    lateinit var mAdView : AdView
    lateinit var testTitleTV:TextView
    lateinit var descriptionTV:TextView
    lateinit var noOfQuesTV:TextView
    lateinit var timerTV:TextView
    lateinit var topTitleTV:TextView
    lateinit var question: Question
    lateinit var startTestActivityFinish : ImageView
    lateinit var testBannerIV : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_test)
        val testId = intent.getStringExtra("testId")
        testTitleTV = findViewById(R.id.testTitleTV)
        descriptionTV = findViewById(R.id.descriptionTV)
        noOfQuesTV= findViewById(R.id.noOfQuesTV)
        timerTV = findViewById(R.id.timerTV)
        topTitleTV = findViewById(R.id.topTitleTV)
        startTestActivityFinish = findViewById(R.id.startTestActivityFinishBtn)
        testBannerIV = findViewById(R.id.testBannerIV)
        question = Question()
        startTestActivityFinish.setOnClickListener(View.OnClickListener {
            finish()
        })
        pd = ProgressDialog(this)
        pd.setTitle("Loading Test")
        pd.setMessage("Please Wait..")
        pd.setCancelable(false)
        pd.show()
        startTestBtn = findViewById(R.id.startTestBtn)
        startTestBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,AttemptTestActivity::class.java)
            intent.putExtra("noOfQues",noOfQues)
            intent.putExtra("timeAllowed",timeAllowed)
            val gson = Gson()
            val serializedTest = gson.toJson(test)
            Log.i("serialised", serializedTest)
            intent.putExtra("TEST", serializedTest);
            startActivity(intent)
        })

        FirebaseFirestore.getInstance().collection("directory").document(testId!!).get().addOnCompleteListener{
            if(it.isSuccessful){
                if(it.result.exists()) {
                    Log.i("abh", "onCreate:on tesetId ${testId} test: ${it.toString()}")
                    test = it.result.toObject(Test::class.java)!!
                    currentTestTitle = test.testTitle.toString()
                    currentTestDescription = test.testDescription.toString()
                    noOfQues = (test.questions.size)
                    timeAllowed = test.timeAllowed.toString()
                    for (index in 0.. noOfQues-1){
                    Log.i("adi test", test.questions.get(index).question.toString())}
                    if (currentTestTitle.isNotEmpty()){
                        testTitleTV.text = currentTestTitle
                        topTitleTV.text = currentTestTitle
                    }
                    if (currentTestDescription.isNotEmpty()){
                        descriptionTV.text = currentTestDescription
                    }
                    if (noOfQues !=0){
                        noOfQuesTV.text = test.questions.size.toString()
                        Log.i("adi noques", test.questions.size.toString())
                    }
                    if (timeAllowed.isNotEmpty()){
                        timerTV.text = timeAllowed
                    }
                    pd.dismiss()
                }
            }
        }


        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)



}


}