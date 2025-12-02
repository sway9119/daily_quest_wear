package data.repository

import android.util.Log
import data.api.GitHubService
import data.response.Contribution
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitHubRepository(
    private val gitHubService: GitHubService
) {
    companion object {
        private const val TAG = "GitHubRepository"
    }

    // 今日のGitHubコントリビューションデータを取得するメソッド
    fun getTodayContributions(callback: (Int?) -> Unit) {
        Log.d(TAG, "GitHub API呼び出し開始")

        gitHubService.getTodayContributions().enqueue(object : Callback<Contribution> {
            override fun onResponse(call: Call<Contribution>, response: Response<Contribution>) {
                if (response.isSuccessful) {
                    val count = response.body()?.count
                    Log.d(TAG, "GitHub API成功: count=$count, body=${response.body()}")
                    callback(count)
                } else {
                    Log.e(TAG, "GitHub API失敗: code=${response.code()}, message=${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Contribution>, t: Throwable) {
                Log.e(TAG, "GitHub API通信エラー: ${t.message}", t)
                callback(null)
            }
        })
    }
}