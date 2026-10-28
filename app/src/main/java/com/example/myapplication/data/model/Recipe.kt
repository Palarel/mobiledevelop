package com.example.myapplication.data.model

data class Ingredient(
    val name: String,
    val baseAmount: Double,
    val unit: String
)

data class RecipeStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val timerSeconds: Int? = null
)

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val prepTimeMinutes: Int,
    val difficulty: String,
    val baseServings: Int = 2,
    val ingredients: List<Ingredient>,
    val steps: List<RecipeStep>
)
