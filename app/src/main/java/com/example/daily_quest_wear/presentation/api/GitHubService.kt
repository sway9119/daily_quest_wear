package com.example.daily_quest_wear.presentation.api

import Contribution
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("/users/{username}/contributions")
    fun getContributions(@Path("username") username: String): Call<List<Contribution>>
}
