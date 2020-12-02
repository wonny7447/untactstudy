package com.dongguk.untactstudy.Model

class TodoCountData(
        val completeCount : Int,
        val totalCount : Int
) {
        constructor() : this(0, 0)
}