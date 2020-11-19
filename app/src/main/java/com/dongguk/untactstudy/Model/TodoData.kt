package com.dongguk.untactstudy.Model

class TodoData(
        val list : MutableList<String>
) {
        constructor() : this(listOf("") as MutableList<String>)
}