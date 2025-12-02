package com.example.daily_quest_wear.presentation.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.api.RetrofitClient
import data.repository.GitHubRepository
import kotlinx.coroutines.launch

class QuestViewModel : ViewModel() {
    companion object {
        private const val TAG = "QuestViewModel"
    }

    private val gitHubRepository = GitHubRepository(RetrofitClient.gitHubService)
    private val _questList = mutableStateOf<List<Quest>>(emptyList())
    val questList = _questList

    init {
        // テスト用のクエストリストを生成します。実際のデータ取得ロジックに置き換えてください。
        _questList.value = listOf(
            Quest(
                id = "1",
                name = "腕立て伏せ",
                targetValue = 10,
                currentValue = 0,
                unit = "回",
                detectionType = DetectionType.MANUAL
            ),
            Quest(
                id = "2",
                name = "ランニング",
                targetValue = 5,
                currentValue = 0,
                unit = "km",
                detectionType = DetectionType.HEALTHKIT
            ),
            Quest(
                id = "3",
                name = "読書",
                targetValue = 60,
                currentValue = 0,
                unit = "分",
                detectionType = DetectionType.MANUAL
            ),
            Quest(
                id = "4",
                name = "Githubにコントリビュート",
                targetValue = 1,
                currentValue = 0,
                unit = "回",
                detectionType = DetectionType.GITHUB
            )
        )

        // 初期化時にGitHubコントリビューション数を取得
        checkGithub()
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