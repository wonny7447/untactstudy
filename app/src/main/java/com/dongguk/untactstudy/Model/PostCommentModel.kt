package com.dongguk.untactstudy.Model

class PostCommentModel (
        val time : String,
        val userName : String,
        val userUid : String,
        val comment : String,
        val postDocumentId : String
){

        constructor() : this("", "","","","")
}

