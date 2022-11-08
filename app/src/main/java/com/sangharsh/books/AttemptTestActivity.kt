package com.sangharsh.books

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.sangharsh.books.model.Question
import com.sangharsh.books.model.Test
import java.util.*

class AttemptTestActivity : AppCompatActivity() ,View.OnClickListener {
    lateinit var questionTV : TextView
    lateinit var optionATV : TextView
    lateinit var optionBTV : TextView
    lateinit var optionCTV : TextView
    lateinit var optionDTV : TextView
    lateinit var noofQuesTV : TextView
    lateinit var tv : View
    lateinit var timerTV : TextView
    lateinit var backTestCrossIV : ImageView
    lateinit var nextBtn: TextView
    lateinit var backBtnduringGrid: TextView
    lateinit var backBtn: TextView
    lateinit var testll:LinearLayout
    lateinit var bottomBtnLinearLayoutDuringGrid:LinearLayout
    lateinit var bottomBtnsLinearLayout:LinearLayout
    lateinit var gridTV:TextView
    lateinit var gridL:GridLayout
    val options = ArrayList<TextView>()
     var  noOfQues:Int =0
    private lateinit var currentQuestion: Question
    private var timeAllowed :Long =0
    private var mselectedOptionPosition: Int =-1
    private lateinit var test: Test
    private var currentQuestionNo:Int =1
    lateinit var mAdView : AdView
    var isLastQuestionreached:Boolean = false
     var isGridVisible:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_test)

        questionTV = findViewById(R.id.questionTV)
        optionATV = findViewById(R.id.optionATV)
        optionBTV = findViewById(R.id.optionBTV)
        optionCTV = findViewById(R.id.optionCTV)
        optionDTV = findViewById(R.id.optionDTV)
        noofQuesTV = findViewById(R.id.noofQuesTV)
        timerTV = findViewById(R.id.timertv)
        nextBtn = findViewById(R.id.nextBtnTV)
        backBtn = findViewById(R.id.backBtn)
        backBtnduringGrid = findViewById(R.id.backBtnduringGrid)
        backTestCrossIV = findViewById(R.id.backCrossIV)
        gridL = findViewById(R.id.quesDrawerGrid)
        bottomBtnsLinearLayout = findViewById(R.id.bottomBtnsLinearLayout)
        bottomBtnLinearLayoutDuringGrid = findViewById(R.id.bottomBtnLinearLayoutDuringGrid)
        currentQuestion = Question()
        testll = findViewById(R.id.testll)
        val testDes = intent.getStringExtra("TEST")
        Log.i("testDes", testDes!!)
//        gridTV = findViewById(R.id.gridTV)

        test = Gson().fromJson(intent.getStringExtra("TEST"), Test::class.java)
        Log.i("test", intent.getStringExtra("TEST").toString())
        Log.i("adi noques attempt",(test.questions.size+1).toString())

        options.add(0,optionATV)
        options.add(1,optionBTV)
        options.add(2,optionCTV)
        options.add(3,optionDTV)
        questionTV.setOnClickListener(this)
        optionATV.setOnClickListener(this)
        optionBTV.setOnClickListener(this)
        optionCTV.setOnClickListener(this)
        optionDTV.setOnClickListener(this)
        updateQuestion(ques = currentQuestion,0)
        backTestCrossIV.setOnClickListener(View.OnClickListener {
            showAlert()
        })
        val timer= intent.getStringExtra("timeAllowed")
        timeAllowed = timer!!.toLong()
        object : CountDownTimer(timeAllowed*60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var minutes:Long = millisUntilFinished/1000/60
                var seconds:Long = millisUntilFinished/1000%60
                var timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds)
                timerTV.text =timeLeftFormat
            }
            override fun onFinish() {
                launchResultActivity()
            }
        }.start()
        noOfQues = test.questions.size.toInt()
        setQuesNoinTV()
        nextBtn.setOnClickListener(View.OnClickListener {
            if (isLastQuestionreached)
                launchResultActivity()
            else
                nextQuestion()
        })
        backBtn.setOnClickListener(View.OnClickListener {
            if (currentQuestionNo>1){
                currentQuestionNo--
                setQuesNoinTV()
                updateQuestion(ques =currentQuestion,currentQuestionNo-1)
            }
            setButtonUI()
        })
        backBtnduringGrid.setOnClickListener(View.OnClickListener {
            showTestLayout()
        })


        noofQuesTV.setOnClickListener(View.OnClickListener {
            if(!isGridVisible)
            showGridLayout()
            else
                showTestLayout()
        })

        for(index in 1..test.questions.size){
             tv = layoutInflater.inflate(R.layout.question_testbox,null)
            tv.tag = index
            gridL.addView(tv)
            gridL.findViewWithTag<TextView>(index).text= index.toString()
            onClickEventInGrid(index)
        }
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun onClickEventInGrid(index:Int){
        tv.setOnClickListener(View.OnClickListener {
            showTestLayout()
            currentQuestionNo = index
            setQuesNoinTV()
            updateQuestion(ques = currentQuestion,index-1)
        })
    }

    private fun nextQuestion(){
        if (mselectedOptionPosition == -1){
            addAnswers()
        }
        mselectedOptionPosition = -1;
        currentQuestionNo++
        setQuesNoinTV()
        updateQuestion(ques = currentQuestion,currentQuestionNo-1)
        setButtonUI()
    }


    fun launchResultActivity(){
        if (mselectedOptionPosition == -1){
            addAnswers()
        }
        Log.i("hashmap", answers.toString())
        val selectedStr = Gson().toJson(answers)
        val testStr = Gson().toJson(test);
        val intent = Intent(this,ResultActivity::class.java)
        intent.putExtra("TEST", testStr)
        intent.putExtra("SELECTED", selectedStr)
        startActivity(intent)
        finish()
    }

    fun updateQuestion(ques: Question,currentQuesNo:Int){
        currentQuestion = test.questions[currentQuesNo]
        questionTV.text = currentQuestion.question
        optionATV.text = currentQuestion.option1
        optionBTV.text = currentQuestion.option2
        optionCTV.text = currentQuestion.option3
        optionDTV.text = currentQuestion.option4
        setDefaultOptionsUI()
        if(answers.containsKey(currentQuestionNo)) {
            Log.i("FuncHit", "contains key ${answers[currentQuestionNo]}")
            mselectedOptionPosition = answers[currentQuestionNo]!!
            setOptionUI()
        }
        setButtonUI()
        Log.i("currentQues", currentQuestionNo.toString())
    }

    private fun setButtonUI() {
        if(currentQuestionNo == test.questions.size)
            nextBtn.text = "Submit"
        else
            nextBtn.text = "Next"
        isLastQuestionreached = (currentQuestionNo == test.questions.size)
    }

    private var answers = HashMap<Int, Int>()
    private fun addAnswers() {
        answers[currentQuestionNo] = mselectedOptionPosition
    }

    private fun setQuesNoinTV(){
        if (noOfQues!=0 ) {
            val noOfQuesTVtext = "${currentQuestionNo}/${noOfQues}"
            noofQuesTV.text = noOfQuesTVtext
        }
    }

    fun backTest(view: View) {
        val intent = Intent(this,StartTest::class.java)
        startActivity(intent)
    }

    private fun setDefaultOptionsUI() {
        for (option in options){
            option.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            option.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.optionATV->{
                selectOption(0)
            }
            R.id.optionBTV->{
                selectOption(1)
            }
            R.id.optionCTV->{
                selectOption(2)
            }
            R.id.optionDTV->{
                selectOption(3)
            }
        }
    }
    override fun onBackPressed() {
        showAlert()
    }
    fun setOptionUI(){
        setDefaultOptionsUI()
        if (mselectedOptionPosition == -1)
            return
        val tv = options[mselectedOptionPosition];
        tv.background = ContextCompat.getDrawable(this, R.drawable.selctedoptionbg)
        tv.setTextColor(android.graphics.Color.parseColor("#BB0B14"))
    }
    fun selectOption(o: Int){
        if(mselectedOptionPosition == o)
            mselectedOptionPosition = -1;
        else
            mselectedOptionPosition = o
        setOptionUI()
        addAnswers()
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you really want to exit ?")
        builder.setTitle("Your progress will be deleted!!!")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") {
                dialog, which -> finish()
        }
        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showTestLayout(){
        testll.visibility = View.VISIBLE
        gridL.visibility = View.GONE
        bottomBtnsLinearLayout.visibility = View.VISIBLE
        bottomBtnLinearLayoutDuringGrid.visibility = View.GONE
        isGridVisible = false
    }
    fun showGridLayout(){
        testll.visibility = View.GONE
        gridL.visibility = View.VISIBLE
        bottomBtnLinearLayoutDuringGrid.visibility = View.VISIBLE
        bottomBtnsLinearLayout.visibility = View.GONE
        isGridVisible = true
        for(index in 1 .. test.questions.size){
            if (answers[index] == -1){
                Log.i("color", "$index is -1 no selected")
                gridL.findViewWithTag<TextView>(index).background = ContextCompat.getDrawable(this,R.drawable.gridtvbgred)

            }
            else if(answers[index]==0||answers[index]==1||answers[index]==2||answers[index]==3){
                Log.i("color", "$index is selected")

                gridL.findViewWithTag<TextView>(index).background = ContextCompat.getDrawable(this,R.drawable.gridtvbggreen)
            }
        }
    }
}


