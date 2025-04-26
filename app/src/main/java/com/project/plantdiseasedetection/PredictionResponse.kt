package com.project.plantdiseasedetection

data class PredictionResponse(
    val prediction_index: Int,
    val disease_name: String,
    val description: String,
    val prevention: String,
    val image_url: String,
    val supplement: Supplement?
)

data class Supplement(
    val name: String?,
    val image_url: String?,
    val buy_link: String?
)
