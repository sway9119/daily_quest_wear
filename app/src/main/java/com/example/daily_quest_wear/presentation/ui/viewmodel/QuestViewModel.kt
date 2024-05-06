package com.example.daily_quest_wear.presentation.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.repository.GitHubRepository
import data.response.Contribution
import kotlinx.coroutines.launch

class QuestViewModel(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {
    private val _questList = mutableStateOf<List<Quest>>(emptyList())
    val questList = _questList

    init {
        // テスト用のクエストリストを生成します。実際のデータ取得ロジックに置き換えてください。
        _questList.value = listOf(
            Quest("Githubにコントリビュート", false),
            Quest("ストレッチをする", false),
            Quest("本を読む", false)
        )
    }

    // isCheckedの値を更新するメソッドを追加します。
    fun updateQuestCheckedState(quest: Quest, isChecked: Boolean) {
        _questList.value = _questList.value.map {
            if (it == quest) {
                it.copy(isChecked = isChecked)
            } else {
                it
            }
        }
    }

    fun checkGithub() {
        val username = "test"
        viewModelScope.launch {
            gitHubRepository.getContributions(username) { contributions ->
                val hasContributions = contributions?.isNotEmpty() == true
                updateQuestCheckedState(
                    _questList.value.find { it.name == "Githubにコントリビュート" }!!,
                    hasContributions
                )
            }
        }
    }
}

data class Quest(val name: String, val isChecked: Boolean)