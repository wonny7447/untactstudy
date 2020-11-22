package com.dongguk.untactstudy.Model

class LoginUserData(val uid : String,
                    val userName : String,
                    val userEmail : String,
                    val userPhotoUrl : String,
                    val time : Long,
                    val introduction : String,
                    val studyRoomNumber : String,
                    val leader : Boolean,
                    var totalRating : Float,
                    var totalRater : Long,
                    var onStudy : Boolean) {

    constructor() : this("","","","",0,"", "", false, 0f, 0, false)

}