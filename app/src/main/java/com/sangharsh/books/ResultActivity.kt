package com.sangharsh.books

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
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
import com.google.gson.Gson
import com.sangharsh.books.model.Test
import java.lang.String


class ResultActivity : AppCompatActivity() {
    lateinit var currTest:Test
    var settingOptionNo:Int = 0
    lateinit var titleTV :TextView
    private lateinit var correctAnswerTV :TextView
    private lateinit var incorrectAnswerTV :TextView
    private lateinit var unattemptedTV :TextView
    private lateinit var backBtnImg :ImageView
    private lateinit var questionIV :ImageView
    private lateinit var optionAIV :ImageView
    private lateinit var optionBIV :ImageView
    private lateinit var optionCIV :ImageView
    lateinit var optionDIV :ImageView
    lateinit var gridL: GridLayout
    lateinit var resultScrollView: ScrollView
    lateinit var questionTVResult: TextView
    lateinit var optionATVResult: TextView
    lateinit var optionBTVResult: TextView
    lateinit var optionCTVResult: TextView
    lateinit var optionDTVResult: TextView
    lateinit var optionAResultLL : LinearLayout
    lateinit var optionBResultLL : LinearLayout
    lateinit var optionCResultLL : LinearLayout
    lateinit var optionDResultLL : LinearLayout
    lateinit var questionsResultLL : LinearLayout
    lateinit var resultLL : LinearLayout
    lateinit var backBtnLL : LinearLayout
    private lateinit var tv : View
    lateinit var selectedOptions:HashMap<String,Int>
    var correctAnswers :Int =0
    var incorrectAnswers :Int =0
    var unattemptedQuestions :Int =0
    private lateinit var test: Test
    lateinit var pieChart: PieChart
    lateinit var mAdView : AdView
    lateinit var mAdView2 : AdView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        titleTV = findViewById(R.id.titleTV)
        correctAnswerTV = findViewById(R.id.correctAnswerTV)
        incorrectAnswerTV = findViewById(R.id.incorrectAnswerTV)
        unattemptedTV = findViewById(R.id.unattemptedTV)
        backBtnImg = findViewById(R.id.backBtnImg)
        gridL = findViewById(R.id.quesDrawerGrid)
        questionIV = findViewById(R.id.questionIV)
        optionAIV = findViewById(R.id.optionAIV)
        optionBIV = findViewById(R.id.optionBIV)
        optionCIV = findViewById(R.id.optionCIV)
        optionDIV = findViewById(R.id.optionDIV)
        resultScrollView = findViewById(R.id.resultScrollView)
        questionTVResult = findViewById(R.id.questionTVResult)
        optionATVResult = findViewById(R.id.optionATVResult)
        optionBTVResult = findViewById(R.id.optionBTVResult)
        optionCTVResult = findViewById(R.id.optionCTVResult)
        optionDTVResult = findViewById(R.id.optionDTVResult)
        optionAResultLL = findViewById(R.id.optionAResultLL)
        optionBResultLL = findViewById(R.id.optionBResultLL)
        optionCResultLL = findViewById(R.id.optionCResultLL)
        optionDResultLL = findViewById(R.id.optionDResultLL)
        questionsResultLL = findViewById(R.id.questionsResultLL)
        resultLL = findViewById(R.id.resultLL)
        backBtnLL = findViewById(R.id.backBtnLL)






        test = Gson().fromJson(intent.getStringExtra("TEST"), Test::class.java)

        for(index in 1..test.questions.size) {
            showImages(index)
        }


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
            tv.setOnClickListener(View.OnClickListener {
                resultLL.visibility= View.GONE
                questionsResultLL.visibility = View.VISIBLE
                resultScrollView.visibility = View.VISIBLE
                backBtnLL.visibility = View.VISIBLE
                setQuestionInLayout(index)
                setOptionsUI(index)
                setCorrectOptionUI(index)
            })
        }


        pieChart()
        showGridLayout()
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        mAdView2 = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView2.loadAd(adRequest)

        backBtnLL.setOnClickListener(View.OnClickListener {
            resultLL.visibility = View.VISIBLE
            questionsResultLL.visibility = View.GONE
            backBtnLL.visibility = View.GONE

        })


    }



    private fun setQuestionInLayout(index: Int) {

//        if(isQuestionsRequested){
            resultScrollView.visibility = View.VISIBLE
            questionTVResult.text = test.questions[index-1].question
            optionATVResult.text = test.questions[index-1].option1
            optionBTVResult.text = test.questions[index-1].option2
            optionCTVResult.text = test.questions[index-1].option3
            optionDTVResult.text = test.questions[index-1].option4
//        }

    }

    private fun showImages(index:Int) {
        if(test.questions[index-1].quesImgUrl!= null
            ||test.questions[index-1].option1ImgUrl!=null
            ||test.questions[index-1].option2ImgUrl!= null
            ||test.questions[index-1].option3ImgUrl!=null
            ||test.questions[index-1].option4ImgUrl!= null){

            if(test.questions[index-1].quesImgUrl!= null)
                questionIV.visibility = View.VISIBLE
            //TODO set the images from the link
            if(test.questions[index-1].option1ImgUrl!= null)
                optionAIV.visibility = View.VISIBLE
            if(test.questions[index-1].option2ImgUrl!= null)
                optionBIV.visibility = View.VISIBLE
            if(test.questions[index-1].option3ImgUrl!= null)
                optionCIV.visibility = View.VISIBLE
            if(test.questions[index-1].option4ImgUrl!= null)
                optionDIV.visibility = View.VISIBLE
        }
        else{
            Log.i("images", "images adresses are null")
        }


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


    private fun pieChart(){
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


    private fun setOptionsUI(index: Int){
        if (!selectedOptions.containsKey("$index" as String)
            || selectedOptions["$index" as String]!!.toInt() == -1 ){
            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)


        }
        else if(selectedOptions["$index" as String]!!.toInt()==0
            ){
            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.selctedoptionbg)
            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
        }
        else if (selectedOptions["$index" as String]!!.toInt()==1){
            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.selctedoptionbg)

            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)

        }
        else if (selectedOptions["$index" as String]!!.toInt()==2){
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.selctedoptionbg)

            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)

        }
        else if (selectedOptions["$index" as String]!!.toInt()==3){
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.selctedoptionbg)
            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)
            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.optionwhitbg)

        }
    }

    private fun setCorrectOptionUI(index: Int) {
        if(test.questions[index-1].correctOption == 0){
            optionAResultLL.background = ContextCompat.getDrawable(this,R.drawable.greenoptionbg)
        }
        else if(test.questions[index-1].correctOption == 1){
            optionBResultLL.background = ContextCompat.getDrawable(this,R.drawable.greenoptionbg)
        }
        else if(test.questions[index-1].correctOption == 2){
            optionCResultLL.background = ContextCompat.getDrawable(this,R.drawable.greenoptionbg)
        }
        else if(test.questions[index-1].correctOption == 3){
            optionDResultLL.background = ContextCompat.getDrawable(this,R.drawable.greenoptionbg)
        }

    }

    private fun showGridLayout(){
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