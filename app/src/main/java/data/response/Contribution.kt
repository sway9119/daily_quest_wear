package data.response

import com.google.gson.annotations.SerializedName

data class Contribution(
    @SerializedName("date") val date: String,
    @SerializedName("count") val count: Int
)
