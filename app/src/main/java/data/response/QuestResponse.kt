package data.response

import com.google.gson.annotations.SerializedName

data class QuestResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("targetValue") val targetValue: Int,
    @SerializedName("currentValue") val currentValue: Int = 0,
    @SerializedName("unit") val unit: String,
    @SerializedName("detectionType") val detectionType: String = "MANUAL"
)
