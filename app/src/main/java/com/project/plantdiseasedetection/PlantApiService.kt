package com.project.plantdiseasedetection

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlantApiService {
    @Multipart
    @POST("api/predict")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<PredictionResponse>
}
