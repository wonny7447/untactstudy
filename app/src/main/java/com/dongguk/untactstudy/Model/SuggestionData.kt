package com.dongguk.untactstudy.Model

class SuggestionData (
    var suggestion: String,
    var evalUid : String,
    var evalUserName : String,
    var evalTargetUid : String,
    var evalTargetName : String,
    var rating : Float
) {
    constructor() : this("", "", "", "", "", 0f)
}