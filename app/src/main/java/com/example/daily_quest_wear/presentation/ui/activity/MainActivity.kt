package com.example.daily_quest_wear.presentation.ui.activity

import Quest
import QuestViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Checkbox
import kotlinx.coroutines.delay
import java.util.*

class MainActivity : ComponentActivity() {
    private val questViewModel by viewModels<QuestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyQuestWearApp(questViewModel)
        }
    }
}

@Composable
fun DailyQuestWearApp(viewModel: QuestViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CountdownTimer()
        DailyQuestBox(viewModel)
    }
}

@Composable
fun CountdownTimer() {
    var remainingTime by remember { mutableStateOf(calculateRemainingTime()) }

    LaunchedEffect(true) {
        while (true) {
            remainingTime = calculateRemainingTime()
            delay(60000) // Update every minute
        }
    }

    Text(
        text = "残り時間: $remainingTime",
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxWidth()
    )
}

fun calculateRemainingTime(): String {
    val currentTime = Calendar.getInstance()
    val nextDay = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val diff = nextDay.timeInMillis - currentTime.timeInMillis
    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60
    return String.format("%02d:%02d", hours, minutes)
}

@Composable
fun DailyQuestBox(viewModel: QuestViewModel) {
    // Placeholder content, replace with actual daily quests
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        // ViewModelからQuestリストを取得して表示します。
        viewModel.questList.value.forEach { quest ->
            QuestItem(quest, viewModel::updateQuestCheckedState)
        }
    }
}

@Composable
fun QuestItem(quest: Quest, onCheckedChange: (Quest, Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        Checkbox(
            checked = quest.isChecked,
            onCheckedChange = { isChecked ->
                onCheckedChange(quest, isChecked)
            },
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = quest.name,
            fontSize = 13.sp,
            overflow = TextOverflow.Visible
        )
    }
}



@Composable
fun TextButton(onClick: () -> Unit, modifier: Modifier, colors: Any, content: @Composable () -> Unit) {
    // ここにボタンの実装を追加
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
//    DailyQuestWearApp(questViewModel)
}
