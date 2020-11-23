package com.dongguk.untactstudy.Adapter

import android.content.ClipData
import com.dongguk.untactstudy.Model.LoginUserData
import com.dongguk.untactstudy.Model.addpostModel
import com.dongguk.untactstudy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.message_list_row.view.*

class postItem (val title : String, val body : String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int{
        return R.layout.post_list_row
    }
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name.text = title
        viewHolder.itemView.msg.text = body

    }
}