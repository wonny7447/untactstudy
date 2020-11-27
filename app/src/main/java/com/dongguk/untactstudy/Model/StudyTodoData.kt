package com.dongguk.untactstudy.Model

class StudyTodoData(
        val countList : MutableList<Int>,
        val list : MutableList<String>
) {
        constructor() : this(listOf(0) as MutableList<Int>, listOf("") as MutableList<String>)
}