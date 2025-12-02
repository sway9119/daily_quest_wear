package data.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val TAG = "RetrofitClient"

    // adb reverse でポートフォワーディング設定済み
    // エミュレータから localhost でホストマシンにアクセス可能
    // 実機の場合は開発マシンのIPアドレスを使用（例: 192.168.x.x）
    private const val BASE_URL = "http://localhost:8080/"

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
}
