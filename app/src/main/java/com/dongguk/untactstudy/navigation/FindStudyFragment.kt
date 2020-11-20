package com.dongguk.untactstudy.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongguk.untactstudy.Adapter.Recycler
import com.dongguk.untactstudy.CreateStudyActivity
import com.dongguk.untactstudy.FindStudyActivity
import com.dongguk.untactstudy.R
import com.google.firebase.firestore.FirebaseFirestore


class FindStudyFragment : Fragment(){

    var mainView: View? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainView = LayoutInflater.from(activity).inflate(
                R.layout.fragment_findstudy,
                container,
                false
        )

        return mainView

    }

    override fun onResume() {
        super.onResume()
        //val intent = Intent(context, FindStudyActivity::class.java)
        //startActivity(intent)
    }

}