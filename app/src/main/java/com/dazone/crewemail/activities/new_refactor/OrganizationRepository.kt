package com.dazone.crewemail.activities.new_refactor

import com.google.gson.JsonObject
import com.kunpark.resource.services.Result

/**
 * Created by BM Anderson on 04/07/2022.
 */
class OrganizationRepository: BaseRepository() {
    suspend fun getDepartments(param: JsonObject): Result<BaseResponse> {
        return safeApiCall(call = { RetrofitFactory.getApiService().getDepartments(param)}, errorMessage =  "Cannot get Departments data from server!")
    }

    suspend fun getUserByDepartmentNo(param: JsonObject): Result<BaseResponse> {
        return safeApiCall(call = { RetrofitFactory.getApiService().getUserByDepartmentNo(param)}, errorMessage =  "Cannot get User by departmentNo data from server!")
    }

    suspend fun getDepartmentMod(param: JsonObject): Result<BaseResponse> {
        return safeApiCall(call = { RetrofitFactory.getApiService().getDepartmentMod(param)}, errorMessage =  "Cannot get Department Mod from server!")
    }

    suspend fun getMemberMod(param: JsonObject): Result<BaseResponse> {
        return safeApiCall(call = { RetrofitFactory.getApiService().getMemberMod(param)}, errorMessage =  "Cannot User Mod data from server!")
    }

}