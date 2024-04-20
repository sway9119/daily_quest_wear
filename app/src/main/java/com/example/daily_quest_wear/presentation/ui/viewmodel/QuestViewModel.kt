import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class QuestViewModel : ViewModel() {
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
}

data class Quest(val name: String, val isChecked: Boolean)
