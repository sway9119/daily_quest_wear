package com.example.daily_quest_wear.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Checkbox
import kotlinx.coroutines.delay
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyQuestWearApp()
        }
    }
}

@Composable
fun DailyQuestWearApp() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CountdownTimer()
        DailyQuestBox()
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
fun DailyQuestBox() {
    // Placeholder content, replace with actual daily quests
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        QuestItem(text = "タスクを完了する", isChecked = false)
        QuestItem(text = "ストレッチをする", isChecked = false)
        QuestItem(text = "本を読む", isChecked = false)
    }
}

@Composable
fun QuestItem(text: String, isChecked: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null, // チェックボックスの機能を無効にする
            enabled = false, // チェックボックスを無効にする
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            textAlign = TextAlign.Start // テキストの先頭を揃える
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
    DailyQuestWearApp()
}
