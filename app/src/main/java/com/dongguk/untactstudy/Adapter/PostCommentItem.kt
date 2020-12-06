package com.dongguk.untactstudy.Adapter

import com.dongguk.untactstudy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.post_comment_row.view.*

class PostCommentItem (val commentUserName : String, val postComment : String) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.post_comment_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.commentUserName.text = commentUserName
        viewHolder.itemView.postComment.text = postComment
    }
}