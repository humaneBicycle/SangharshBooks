package com.sangharsh.books

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
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
    lateinit var gridL: GridLayout
    lateinit var tv : View
    lateinit var selectedOptions:HashMap<String,Int>
    var correctAnswers :Int =0
    var incorrectAnswers :Int =0
    var unattemptedQuestions :Int =0
    private lateinit var test: Test
    lateinit var pieChart: PieChart
    private var answers = java.util.HashMap<Int, Int>()
    lateinit var mAdView:AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        titleTV = findViewById(R.id.titleTV)
        correctAnswerTV = findViewById(R.id.correctAnswerTV)
        incorrectAnswerTV = findViewById(R.id.incorrectAnswerTV)
        unattemptedTV = findViewById(R.id.unattemptedTV)
        backBtnImg = findViewById(R.id.backBtnImg)
        gridL = findViewById(R.id.quesDrawerGrid)
        test = Gson().fromJson(intent.getStringExtra("TEST"), Test::class.java)


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


        for(index in 1..test.questions.size){
            tv = layoutInflater.inflate(R.layout.question_testbox,null)
            tv.tag = index
            gridL.addView(tv)
            gridL.findViewWithTag<TextView>(index).text= index.toString()
        }

        Log.i("compare",currTest.questions[0].correctOption.toString() )
        Log.i("compare",currTest.questions[0].correctOption.toString() )
        Log.i("compare",currTest.noOfQuestion.toString() )
        Log.i("compare",correctAnswers.toString() )
        Log.i("result","Unattempted : ${unattemptedQuestions}" )
        Log.i("result","Correct : ${correctAnswers}" )
        Log.i("result","Incorrect : ${incorrectAnswers}" )
        Log.i("result","selected : $selectedOptions" )

        pieChart()
        showGridLayout()

        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    private fun compareCorrectOption(index:Int){
        if (selectedOptions.containsKey("$index" as String)) {
            if (selectedOptions["$index" as String]!!.toInt() == -1){
                unattemptedQuestions++
            } else if (currTest.questions[index - 1].correctOption.toInt() == selectedOptions["$index" as String]!!.toInt()) {
                correctAnswers++
            } else {
                incorrectAnswers++
            }
        } else {
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

    fun showGridLayout(){
        for(index in 1 .. test.questions.size){
            if (!selectedOptions.containsKey("$index" as String)
                || selectedOptions["$index" as String]!!.toInt() == -1 ){
            } else if(selectedOptions["$index" as String]!!.toInt()==currTest.questions[index-1].correctOption){
                gridL.findViewWithTag<TextView>(index).background = ContextCompat.getDrawable(this,R.drawable.gridtvbggreen)
            }
            else{
                gridL.findViewWithTag<TextView>(index).background = ContextCompat.getDrawable(this,R.drawable.gridtvbgred)

            }
        }
    }

}