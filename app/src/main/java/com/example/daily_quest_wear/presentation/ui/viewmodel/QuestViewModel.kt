package com.example.daily_quest_wear.presentation.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.api.RetrofitClient
import data.repository.GitHubRepository
import data.repository.QuestRepository
import data.response.QuestResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class QuestViewModel : ViewModel() {
    companion object {
        private const val TAG = "QuestViewModel"
    }

    private val gitHubRepository = GitHubRepository(RetrofitClient.gitHubService)
    private val questRepository = QuestRepository(RetrofitClient.questService)
    private val _questList = mutableStateOf<List<Quest>>(emptyList())
    val questList = _questList

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    init {
        // APIからクエストリストを取得
        fetchQuests()
    }

    // APIからクエストを取得
    fun fetchQuests() {
        Log.d(TAG, "fetchQuests: クエスト取得開始")
        _isLoading.value = true

        questRepository.getQuests { questResponses ->
            if (questResponses != null) {
                _questList.value = questResponses.map { it.toQuest() }
                Log.d(TAG, "fetchQuests: ${questResponses.size}件のクエストを取得")
                // GitHubクエストがあればコントリビューション数を更新
                checkGithub()
            } else {
                Log.e(TAG, "fetchQuests: クエスト取得失敗")
            }
            _isLoading.value = false
        }
    }

    // QuestResponseをQuestに変換
    private fun QuestResponse.toQuest(): Quest {
        return Quest(
            id = this.id,
            name = this.name,
            targetValue = this.targetValue,
            currentValue = this.currentValue,
            unit = this.unit,
            detectionType = DetectionType.valueOf(this.detectionType)
        )
    }

    // 進捗を更新するメソッド
    fun updateQuestProgress(questId: String, newValue: Int) {
        Log.d(TAG, "updateQuestProgress: questId=$questId, newValue=$newValue")
        val updatedList = _questList.value.map {
            if (it.id == questId) {
                // targetValueを超えても許可する（例: 目標1回でも3回やったら3と表示）
                val finalValue = newValue.coerceAtLeast(0)
                Log.d(TAG, "Quest更新: ${it.name} ${it.currentValue} -> $finalValue (target=${it.targetValue})")
                it.copy(currentValue = finalValue)
            } else {
                it
            }
        }
        _questList.value = updatedList
        Log.d(TAG, "questList更新完了")
    }

    // 完了状態を切り替えるメソッド（旧互換性のため）
    fun toggleQuestCompletion(questId: String) {
        _questList.value = _questList.value.map {
            if (it.id == questId) {
                val newValue = if (it.isCompleted) 0 else it.targetValue
                it.copy(currentValue = newValue)
            } else {
                it
            }
        }
    }

    fun checkGithub() {
        Log.d(TAG, "checkGithub() 呼び出し")
        viewModelScope.launch {
            gitHubRepository.getTodayContributions { contributionCount ->
                Log.d(TAG, "コントリビューション取得結果: $contributionCount")
                val count = contributionCount ?: 0
                val githubQuest = _questList.value.find { it.detectionType == DetectionType.GITHUB }
                Log.d(TAG, "GitHub Quest: $githubQuest")
                githubQuest?.let {
                    Log.d(TAG, "進捗を更新: questId=${it.id}, count=$count")
                    updateQuestProgress(it.id, count)
                }
            }
        }
    }

    // 0時になったら呼ばれる処理を開始
    fun startMidnightResetObserver() {
        viewModelScope.launch {
            var lastCheckedDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

            while (true) {
                delay(1000) // 1秒ごとにチェック

                val currentDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                if (currentDate != lastCheckedDate) {
                    // 日付が変わった（0時を過ぎた）
                    Log.d(TAG, "0時を検出: 日次リセットを実行")
                    onMidnight()
                    lastCheckedDate = currentDate
                }
            }
        }
    }

    // 0時になったときの処理
    private fun onMidnight() {
        Log.d(TAG, "onMidnight: 日次処理を開始")

        // TODO: サーバーへ記録を送信
        sendDailyRecordToServer()

        // クエストをリセット
        resetAllQuests()
    }

    // サーバーへ記録を送信
    private fun sendDailyRecordToServer() {
        val questsToSend = _questList.value
        Log.d(TAG, "sendDailyRecordToServer: ${questsToSend.size}件のクエストを送信予定")

        // TODO: APIサーバーへの送信を実装
        // viewModelScope.launch {
        //     val response = apiService.sendDailyRecord(questsToSend)
        //     if (response.isSuccessful) {
        //         Log.d(TAG, "サーバーへの送信成功")
        //     } else {
        //         Log.e(TAG, "サーバーへの送信失敗: ${response.code()}")
        //     }
        // }
    }

    // 全クエストをリセット
    private fun resetAllQuests() {
        Log.d(TAG, "resetAllQuests: 全クエストの進捗をリセット")
        // APIから最新のクエストリストを再取得
        fetchQuests()
    }
}

data class Quest(
    val id: String,
    val name: String,
    val targetValue: Int,        // 目標値（例: 10回、5km）
    val currentValue: Int = 0,   // 現在値
    val unit: String,             // 単位（例: "回", "km", "時間"）
    val detectionType: DetectionType = DetectionType.MANUAL
) {
    val progressPercentage: Int
        get() = if (targetValue > 0) (currentValue * 100 / targetValue).coerceIn(0, 100) else 0

    val isCompleted: Boolean
        get() = currentValue >= targetValue
}

enum class DetectionType {
    MANUAL,      // 手動
    GITHUB,      // GitHub API
    HEALTHKIT    // Apple HealthKit
}