package com.example.daily_quest_wear.presentation.ui.activity

import android.os.Bundle
import com.example.daily_quest_wear.presentation.ui.viewmodel.Quest
import com.example.daily_quest_wear.presentation.ui.viewmodel.QuestViewModel
import com.example.daily_quest_wear.presentation.ui.viewmodel.DetectionType
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.wear.compose.material.Button
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Divider
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // TODOリスト（スクロール可能）
        DailyQuestList(viewModel)

        // コンパクトヘッダー（前面に固定）
        CompactHeader(viewModel)
    }
}

@Composable
fun CompactHeader(viewModel: QuestViewModel) {
    var remainingTime by remember { mutableStateOf(calculateRemainingTime()) }

    LaunchedEffect(true) {
        while (true) {
            remainingTime = calculateRemainingTime()
            delay(1000)
        }
    }

    val completedCount = viewModel.questList.value.count { it.isCompleted }
    val totalCount = viewModel.questList.value.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⏱ $remainingTime",
            fontSize = 16.sp,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${completedCount}/${totalCount}完了",
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
    }
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
    val seconds = (diff / 1000) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun DailyQuestList(viewModel: QuestViewModel) {
    var selectedQuest by remember { mutableStateOf<Quest?>(null) }
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            top = 60.dp,  // ヘッダーのすぐ下に表示
            bottom = 40.dp,
            start = 16.dp,
            end = 16.dp
        )
    ) {
        items(viewModel.questList.value) { quest ->
            QuestItem(
                quest = quest,
                onQuestClick = { clickedQuest ->
                    if (clickedQuest.detectionType == DetectionType.MANUAL) {
                        selectedQuest = clickedQuest
                    } else {
                        viewModel.toggleQuestCompletion(clickedQuest.id)
                    }
                }
            )
        }
    }

    // 進捗入力ダイアログ
    selectedQuest?.let { quest ->
        ProgressInputDialog(
            quest = quest,
            onDismiss = { selectedQuest = null },
            onProgressUpdate = { newValue ->
                viewModel.updateQuestProgress(quest.id, newValue)
                selectedQuest = null
            }
        )
    }
}

@Composable
fun QuestItem(quest: Quest, onQuestClick: (Quest) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onQuestClick(quest) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // チェックボックス + タイトル
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Checkbox(
                checked = quest.isCompleted,
                onCheckedChange = null, // クリックはColumn全体で処理
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = quest.name,
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        // プログレスバー + 進捗情報
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = quest.progressPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (quest.isCompleted)
                    androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    else androidx.compose.ui.graphics.Color(0xFF2196F3),
                trackColor = androidx.compose.ui.graphics.Color(0xFF424242)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${quest.currentValue}/${quest.targetValue}${quest.unit}",
                fontSize = 10.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun ProgressInputDialog(
    quest: Quest,
    onDismiss: () -> Unit,
    onProgressUpdate: (Int) -> Unit
) {
    var inputValue by remember { mutableStateOf(quest.currentValue) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable(enabled = false) { /* 内部クリックを親に伝播させない */ },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = quest.name,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$inputValue/${quest.targetValue}${quest.unit}",
                fontSize = 20.sp,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // +/- ボタン
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (inputValue > 0) inputValue--
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("-", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (inputValue < quest.targetValue) inputValue++
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("+", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 保存ボタン
            Button(
                onClick = { onProgressUpdate(inputValue) }
            ) {
                Text("保存", fontSize = 12.sp)
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
//    DailyQuestWearApp(questViewModel)
}
