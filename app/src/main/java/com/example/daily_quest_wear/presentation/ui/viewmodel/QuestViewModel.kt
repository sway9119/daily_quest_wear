package com.example.daily_quest_wear.presentation.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.GitHubRepository
import data.response.Contribution
import kotlinx.coroutines.launch

class QuestViewModel(
    private val gitHubRepository: GitHubRepository? = null
) : ViewModel() {
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
    }

    // 進捗を更新するメソッド
    fun updateQuestProgress(questId: String, newValue: Int) {
        _questList.value = _questList.value.map {
            if (it.id == questId) {
                it.copy(currentValue = newValue.coerceIn(0, it.targetValue))
            } else {
                it
            }
        }
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
        val username = "test"
        viewModelScope.launch {
            gitHubRepository?.getContributions(username) { contributions ->
                val contributionCount = contributions?.size ?: 0
                val githubQuest = _questList.value.find { it.detectionType == DetectionType.GITHUB }
                githubQuest?.let {
                    updateQuestProgress(it.id, contributionCount)
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