package data.api

import data.response.QuestResponse
import retrofit2.Call
import retrofit2.http.GET

interface QuestService {
    @GET("/api/quests")
    fun getQuests(): Call<List<QuestResponse>>
}
