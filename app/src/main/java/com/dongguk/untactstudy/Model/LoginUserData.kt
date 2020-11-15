package com.dongguk.untactstudy.Model

class LoginUserData(val uid : String,
                    val userName : String,
                    val userEmail : String,
                    val userPhotoUrl : String,
                    val time : String,
                    val introduction : String) {

    constructor() : this("","","","","","")

}