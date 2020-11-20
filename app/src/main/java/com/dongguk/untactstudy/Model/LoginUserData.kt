package com.dongguk.untactstudy.Model

class LoginUserData(val uid : String,
                    val userName : String,
                    val userEmail : String,
                    val userPhotoUrl : String,
                    val time : Long,
                    val introduction : String,
                    val studyRoomNumber : String,
                    val isLeader : Boolean) {

    constructor() : this("","","","",0,"", "", false)

}