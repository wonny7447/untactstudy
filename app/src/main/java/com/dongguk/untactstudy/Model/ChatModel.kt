package com.dongguk.untactstudy.Model

class ChatModel (val myUid : String,
                 val yourUid : String,
                 val message : String,
                 val time : Long,
                 val who : String,
                 val yourName : String
                 ) {
    constructor() : this("","","",0,"","")
}