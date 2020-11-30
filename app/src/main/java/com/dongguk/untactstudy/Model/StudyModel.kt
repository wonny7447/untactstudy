package com.dongguk.untactstudy.Model

class StudyModel(
    val studyRoomNumber: Long,
    val studyName: String,
    val studyInfo: String,
    val studyMemberAmount: String,
    val sort1st: String,
    val sort2nd: String,
    val userid: String,
    val studyCreateDate : String,
    val studyStartDate : String,
    val studyEndDate : String,
    val quizYN : Boolean,
    val realMemberAmount : Int
) {
    constructor() : this(0, "","","","","","","","","", false, 0)
}