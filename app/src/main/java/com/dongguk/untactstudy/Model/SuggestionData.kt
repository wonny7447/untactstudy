package com.dongguk.untactstudy.Model

class SuggestionData (
    var suggestion: String,
    var evalTargetUid : String,
    var rating : Float
) {
    constructor() : this("", "", 0f)
}