package com.dazone.crewemail.activities.new_refactor

import retrofit2.Response
import java.lang.Exception
import com.kunpark.resource.services.Result

/**
 * Created by BM Anderson on 04/07/2022.
 */
open class BaseRepository() {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): Result<T> {
        return try {
            val myResp = call.invoke()
            if (myResp.isSuccessful) {
                Result.Success(myResp.body()!!)
            } else {
                /*
              handle standard error codes
              if (myResp.code() == 403){
                  Log.i("responseCode","Authentication failed")
              }
              .
              .
              .
               */

                val error = if (myResp.errorBody()?.string().isNullOrEmpty()) errorMessage else myResp.errorBody()?.string()
                    ?: errorMessage
                Result.Error(error)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = if (e.message.isNullOrEmpty()) errorMessage else e.message
                ?: errorMessage
            Result.Error(error)
        }
    }


}