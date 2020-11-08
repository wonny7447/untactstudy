package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.dongguk.untactstudy.CreateStudyActivity
import com.dongguk.untactstudy.MainActivity
import com.dongguk.untactstudy.R
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_mystudy.*

class MyStudyFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_mystudy, container, false)

        var button = view?.findViewById<Button>(R.id.createStudy)
        button?.setOnClickListener(object :View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(context, CreateStudyActivity::class.java)
                startActivity(intent)
            }
        })

        return view
    }





}
