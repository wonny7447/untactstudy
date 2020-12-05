package com.dongguk.untactstudy

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dongguk.untactstudy.Model.ScoreModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizReviewChartActivity : AppCompatActivity() {

    // Log
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review_chart)
        setTitle("Quiz 점수 분포도")

        //var reviewDialView = layoutInflater.inflate(R.layout.activity_quiz_total_review_pop, null)
        //var radarChart : RadarChart? = this.findViewById<RadarChart>(R.id.mapsearchdetail_radar_chart)

        var radarChart: RadarChart = findViewById(R.id.mapsearchdetail_radar_chart)

        var scoreList = ArrayList<Float>()
        var lableList = ArrayList<String>()
        var listSize: Int = 0
        FirebaseFirestore.getInstance().collection("loginUserData")
            .document(FirebaseAuth.getInstance()?.currentUser!!.uid.toString())
            .collection("quizScore")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                scoreList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    var value = snapshot.toObject(ScoreModel::class.java)!!.score
                    var label: String = snapshot.id + "주차"
                    scoreList.add(value)
                    lableList.add(label)
                    Log.e(
                        TAG,
                        "value : " + value + ", listSize : " + listSize + ", label : " + label + ", scoreList : " + scoreList[listSize]
                    )
                    listSize += 1
                }

                Log.e(TAG, "listSize : " + listSize + ", scoreList Size : " + scoreList.size)

                var radarDataSetList: ArrayList<RadarEntry> = ArrayList()

                for(i in 0 .. (listSize - 1 )) {
                    radarDataSetList?.add(RadarEntry(scoreList[i]))

                    Log.e(TAG, "scoreList ("+i+" : "+scoreList[i])
                    Log.e(TAG, "radarDataSetList ("+i+" : "+radarDataSetList?.get(i))
                }

                /*radarDataSetList.add(RadarEntry(scoreList[0]))
                radarDataSetList.add(RadarEntry(scoreList[1]))
                radarDataSetList.add(RadarEntry(scoreList[2]))
                radarDataSetList.add(RadarEntry(scoreList[3]))
                radarDataSetList.add(RadarEntry(scoreList[4]))
                radarDataSetList.add(RadarEntry(scoreList[5]))*/


                Log.e(TAG, "radarDataSetList Size : " + radarDataSetList?.size)

                var dataSet = RadarDataSet(radarDataSetList, "DATA")
                dataSet.setColor(Color.BLUE)
                dataSet.setDrawFilled(true);
                dataSet.setFillAlpha(180);
                dataSet.setLineWidth(2f);

                var radarData: RadarData = RadarData()
                radarData.addDataSet(dataSet)
                radarData.notifyDataChanged()

                var xAxis = radarChart?.getXAxis()
                xAxis?.setValueFormatter(IndexAxisValueFormatter(lableList))
                xAxis?.setAxisMinimum(0f)
                xAxis?.setAxisMaximum(80f)

                var yAxis = radarChart?.getYAxis()
                yAxis?.setAxisMinimum(0f)
                yAxis?.setAxisMaximum(80f)

                radarChart?.setData(radarData)
                /*radarChart?.setExtraTopOffset(2f)
                radarChart?.setExtraBottomOffset(2f)
                radarChart?.setScaleX(2.5f)
                radarChart?.setScaleY(2.5f)*/
                radarChart?.animateXY(1400, 1400, Easing.EaseInOutQuad)

                /*
                *
    xAxis.setTypeface(tfLight);
    xAxis.setTextSize(9f);
    xAxis.setYOffset(0f);
    xAxis.setXOffset(0f);
    yAxis.setTypeface(tfLight);
    yAxis.setLabelCount(5, false);
    yAxis.setTextSize(9f);
    yAxis.setAxisMinimum(0f);
    yAxis.setAxisMaximum(80f);
    yAxis.setDrawLabels(false);
                * */


            }
    } //onCreate
} // activity