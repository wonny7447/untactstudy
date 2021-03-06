package com.dongguk.untactstudy.Adapter

import com.dongguk.untactstudy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_list_row.view.*

class ChatItem (val name : String, val uid : String, val message : String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_list_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name.text = name
        viewHolder.itemView.msg.text = message
    }
}