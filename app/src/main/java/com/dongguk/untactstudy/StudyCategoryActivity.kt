package com.dongguk.untactstudy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

//해결해야할 이슈 : 1차 분류가 어학으로 고정되어 나옴
//저장 버튼을 통해 DB에 분류 내용 저장

class StudyCategoryActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener{

    var spinner1:Spinner? = null
    var spinner2:Spinner? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_category)

        spinner1 = findViewById(R.id.spinner1) as Spinner
        spinner2 = findViewById(R.id.spinner2) as Spinner
        spinner1?.setSelection(0,false)
        spinner1?.setOnItemSelectedListener(this);

    }

    override fun onItemSelected(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ) {
        val sp1 = spinner1!!.selectedItem.toString()
        Toast.makeText(this, sp1, Toast.LENGTH_SHORT).show();

        if (sp1.contentEquals("어학")) {
            val list: MutableList<String> = ArrayList()
            list.add("토익");
            list.add("토플");
            list.add("HSK");
            list.add("JLPT");

            val dataAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dataAdapter.notifyDataSetChanged()
            spinner2!!.adapter = dataAdapter
        }
        if (sp1.contentEquals("자격증")) {
            val list: MutableList<String> = ArrayList()
            list.add("정보처리기사")
            list.add("컴퓨터활용능력")
            list.add("ADSP")
            val dataAdapter2 = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, list
            )
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dataAdapter2.notifyDataSetChanged()
            spinner2!!.adapter = dataAdapter2
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}