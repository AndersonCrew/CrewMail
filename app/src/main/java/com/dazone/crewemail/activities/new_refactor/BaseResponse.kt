package com.dazone.crewemail.activities.new_refactor

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BaseResponse: Serializable {
    @SerializedName("d")
    val response: Any?= null
}