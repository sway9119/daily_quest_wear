package data.repository

import android.util.Log
import data.api.QuestService
import data.response.QuestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestRepository(
    private val questService: QuestService
) {
    companion object {
        private const val TAG = "QuestRepository"
    }

    fun getQuests(callback: (List<QuestResponse>?) -> Unit) {
        Log.d(TAG, "クエスト取得API呼び出し開始")

        questService.getQuests().enqueue(object : Callback<List<QuestResponse>> {
            override fun onResponse(
                call: Call<List<QuestResponse>>,
                response: Response<List<QuestResponse>>
            ) {
                if (response.isSuccessful) {
                    val quests = response.body()
                    Log.d(TAG, "クエスト取得成功: ${quests?.size}件")
                    callback(quests)
                } else {
                    Log.e(TAG, "クエスト取得失敗: code=${response.code()}, message=${response.message()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<QuestResponse>>, t: Throwable) {
                Log.e(TAG, "クエスト取得通信エラー: ${t.message}", t)
                callback(null)
            }
        })
    }
}
