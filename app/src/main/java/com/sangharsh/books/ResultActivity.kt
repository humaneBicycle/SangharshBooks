package com.sangharsh.books

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.sangharsh.books.model.Test
import java.lang.String


class ResultActivity : AppCompatActivity() {
    lateinit var currTest:Test
    var settingOptionNo:Int = 0
    lateinit var titleTV :TextView
    lateinit var correctAnswerTV :TextView
    lateinit var incorrectAnswerTV :TextView
    lateinit var unattemptedTV :TextView
    lateinit var backBtnImg :ImageView
    lateinit var selectedOptions:HashMap<String,Int>
    var correctAnswers :Int =0
    var incorrectAnswers :Int =0
    var unattemptedQuestions :Int =0
    lateinit var pieChart: PieChart
//    override fun onBackPressed() {
//        val intent = Intent(applicationContext, StartTest::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        titleTV = findViewById(R.id.titleTV)
        correctAnswerTV = findViewById(R.id.correctAnswerTV)
        incorrectAnswerTV = findViewById(R.id.incorrectAnswerTV)
        unattemptedTV = findViewById(R.id.unattemptedTV)
        backBtnImg = findViewById(R.id.backBtnImg)

        backBtnImg.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        pieChart = findViewById(R.id.pieChart)

        currTest = Gson().fromJson(intent.getStringExtra("TEST"), Test::class.java)
        selectedOptions = Gson().fromJson(intent.getStringExtra("SELECTED"),HashMap::class.java) as HashMap<String, Int>
        titleTV.text = currTest.testTitle

        for (index in 1 .. currTest.questions.size) {
                compareCorrectOption(index)
        }

        correctAnswerTV.text = correctAnswers.toString()
        incorrectAnswerTV.text = incorrectAnswers.toString()

        unattemptedTV.text = unattemptedQuestions.toString()

        Log.i("compare",currTest.questions[0].correctOption.toString() )
        Log.i("compare",currTest.questions[0].correctOption.toString() )
        Log.i("compare",currTest.noOfQuestion.toString() )
        Log.i("compare",correctAnswers.toString() )
        Log.i("result","Unattempted : ${unattemptedQuestions}" )
        Log.i("result","Correct : ${correctAnswers}" )
        Log.i("result","Incorrect : ${incorrectAnswers}" )

        pieChart()

    }



    private fun compareCorrectOption(index:Int){
        Log.i("Comparison", "starting")
        if (selectedOptions.containsKey("$index" as String)) {
            Log.i("Comparison", "contains")
            if (selectedOptions["$index" as String]!!.toInt() == -1){
                Log.i("Comparison", "una")
                unattemptedQuestions++
            } else if (currTest.questions[index - 1].correctOption.toInt() == selectedOptions["$index" as String]!!.toInt()) {
                Log.i("Comparison", "correct")
                correctAnswers++
            } else {
                Log.i("Comparison", "incorrect")
                incorrectAnswers++
            }
        } else {
            Log.i("Comparison", "Does not contains")
            unattemptedQuestions++
        }

    }
    fun pieChart(){
        val correctAnswersForPieChart = correctAnswers.toFloat()
        val incorrectAnswersForPieChart = incorrectAnswers.toFloat()
        val unattemptedForPieChart = unattemptedQuestions.toFloat()
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(R.color.black)
        pieChart.setTransparentCircleColor(R.color.black)
        pieChart.setTransparentCircleAlpha(100)
        pieChart.setHoleRadius(15f)
        pieChart.setTransparentCircleRadius(15f)
        pieChart.setDrawCenterText(true)
        pieChart.setRotationAngle(0f)
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(correctAnswersForPieChart))
        entries.add(PieEntry(incorrectAnswersForPieChart))
        entries.add(PieEntry(unattemptedForPieChart))
        val dataSet = PieDataSet(entries, "Mobile OS")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.my_green))
        colors.add(resources.getColor(R.color.m_red))
        colors.add(resources.getColor(R.color.m_yellow))
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }

}