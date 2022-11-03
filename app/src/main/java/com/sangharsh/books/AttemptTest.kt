package com.sangharsh.books

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sangharsh.books.model.Question

class AttemptTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attempt_test2)

        //nextbtm.setOnCLick(call-->updatequestion)

//        questionTextViewOptionA.setOnClick(marked answers -> array of string)
    }

    fun updateQuestion(question:Question){
        //questionTextView.setText(question.title)
        //questionTextViewOptionA.setText(question.title)
        //questionTextView.setText(question.title)
        //questionTextView.setText(question.title)
        //questionTextView.setText(question.title)
    }
}