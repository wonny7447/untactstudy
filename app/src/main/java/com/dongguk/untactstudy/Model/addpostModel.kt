package com.dongguk.untactstudy.Model

class addpostModel (
        val title : String,
        val body : String,
        val postphotourl : String,
        val time : Long,
        val userName : String,
        val userUid : String,
        val studyRoomNumber : String
){

        constructor() : this("","","",0, "", "", "")
}

