package com.dongguk.untactstudy.Adapter

import android.net.Uri
import com.dongguk.untactstudy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_attach_left_you.view.*
import kotlinx.android.synthetic.main.chat_attach_right_me.view.*
import kotlinx.android.synthetic.main.chat_left_you.view.*
import kotlinx.android.synthetic.main.chat_left_you.view.left_username

class ChatAttachLeftAndRight(val fileName : String, val userName : String, val directionLR : String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        if(directionLR == "L") {
            return R.layout.chat_attach_left_you
        } else {
            return R.layout.chat_attach_right_me
        }
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if(directionLR == "L") {
            viewHolder.itemView.left_attach_text?.text = fileName
            viewHolder.itemView.left_username.text = userName
            viewHolder.itemView.left_attach.setImageResource(android.R.drawable.ic_menu_save)
        } else {
            viewHolder.itemView.right_attach_text?.text = fileName
            viewHolder.itemView.right_attach_image.setImageResource(android.R.drawable.ic_menu_save)
        }
    }
}