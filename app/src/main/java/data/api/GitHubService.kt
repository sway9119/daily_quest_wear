package data.api

import data.response.Contribution
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("/users/{username}/contributions")
    fun getContributions(@Path("username") username: String): Call<List<Contribution>>
}
