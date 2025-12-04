package data.api

import android.util.Log
import com.example.daily_quest_wear.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val TAG = "RetrofitClient"

    // local.propertiesのAPI_BASE_URLから取得
    private val BASE_URL = BuildConfig.API_BASE_URL

    init {
        Log.d(TAG, "Retrofit初期化: BASE_URL=$BASE_URL")
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val gitHubService: GitHubService by lazy {
        Log.d(TAG, "GitHubService作成")
        retrofit.create(GitHubService::class.java)
    }

    val questService: QuestService by lazy {
        Log.d(TAG, "QuestService作成")
        retrofit.create(QuestService::class.java)
    }
}
