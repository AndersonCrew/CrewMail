package com.dazone.crewemail.activities.new_refactor

import com.dazone.crewemail.activities.new_refactor.BaseResponse
import com.dazone.crewemail.utils.Urls
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DazoneService {

    @POST(Urls.URL_GET_DEPARTMENT)
    suspend fun getDepartments(@Body param: JsonObject): Response<BaseResponse>

    @POST(Urls.URL_GET_USERS_BY_DEPARTMENT)
    suspend fun getUserByDepartmentNo(@Body param: JsonObject): Response<BaseResponse>

    @POST(Urls.URL_GET_DEPARTMENT_MOD)
    suspend fun getDepartmentMod(@Body param: JsonObject): Response<BaseResponse>

    @POST(Urls.URL_GET_USER_MOD)
    suspend fun getMemberMod(@Body param: JsonObject): Response<BaseResponse>
}
