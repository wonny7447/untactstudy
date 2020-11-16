package com.dongguk.untactstudy.Model

import android.net.Uri

class ChatModel(
                    val myUid: String,
                    val yourUid: String,
                    val message: String,
                    val time: String,
                    val who: String,
                    val yourName: String,
                    val photoUri: String,
                    val fileName: String )
{
    constructor() : this("","","","","","","", "")
}