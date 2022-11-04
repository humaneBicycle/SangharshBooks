package com.sangharsh.books

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sangharsh.books.model.Question
import org.w3c.dom.Text

class AttemptTest : AppCompatActivity() {
    lateinit var questionTV :TextView
    lateinit var optionATV :TextView
    lateinit var optionBTV :TextView
    lateinit var optionCTV :TextView
    lateinit var optionDTV :TextView
    lateinit var nextBtn:Button
    lateinit var backBtn:Button
    private lateinit var question:Question
    private var currentQuestionNo:Int =1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_test3)
        questionTV = findViewById(R.id.questionTV)
        optionATV = findViewById(R.id.optionATV)
        optionBTV = findViewById(R.id.optionBTV)
        optionCTV = findViewById(R.id.optionCTV)
        optionDTV = findViewById(R.id.optionDTV)
        nextBtn = findViewById(R.id.nextBtn)
        backBtn = findViewById(R.id.backBtn)

        //nextbtm.setOnCLick(call-->updatequestion)

        nextBtn.setOnClickListener(View.OnClickListener {
            currentQuestionNo++
            updateQuestion(question = question,currentQuestionNo)
        })
        backBtn.setOnClickListener(View.OnClickListener {
            currentQuestionNo--
            updateQuestion(question = question,currentQuestionNo)
        })

//        questionTextViewOptionA.setOnClick(marked answers -> array of string)
    }

//    fun updateQuestion(question:Question){
        //questionTextView.setText(question.title)
        //questionTextViewOptionA.setText(question.title)
        //questionTextView.setText(question.title)
        //questionTextView.setText(question.title)
        //questionTextView.setText(question.title)
//    }
    fun updateQuestion(question: Question,questionNo:Int){

    }
}