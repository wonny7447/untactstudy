package com.dongguk.untactstudy.Model

class QuizModel(
                    val quiz: String,
                    val ex1: String,
                    val ex2: String,
                    val ex3: String,
                    val ex4: String,
                    val answer: String )
{
    constructor() : this("","","","", "", "")
}