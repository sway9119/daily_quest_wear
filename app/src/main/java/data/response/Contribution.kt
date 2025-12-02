package data.response

import com.google.gson.annotations.SerializedName

data class Contribution(
    @SerializedName("date") val date: String,
    @SerializedName("username") val username: String? = null,
    @SerializedName("contribution_count") val count: Int
)
