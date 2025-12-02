package data.api

import data.response.Contribution
import retrofit2.Call
import retrofit2.http.GET

interface GitHubService {
    @GET("/api/github/contributions/today")
    fun getTodayContributions(): Call<Contribution>
}
