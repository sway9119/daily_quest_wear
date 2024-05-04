package data.repository

import data.api.GitHubService
import data.response.Contribution
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitHubRepository(
    private val gitHubService: GitHubService
) {
    // GitHubからコントリビューションデータを取得するメソッド
    fun getContributions(username: String, callback: (List<Contribution>?) -> Unit) {
        gitHubService.getContributions(username).enqueue(object : Callback<List<Contribution>> {
            override fun onResponse(call: Call<List<Contribution>>, response: Response<List<Contribution>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<Contribution>>, t: Throwable) {
                callback(null)
            }
        })
    }
}