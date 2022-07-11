package com.dazone.crewemail.activities.new_refactor

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dazone.crewemail.DaZoneApplication
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.utils.Prefs
import com.dazone.crewemail.utils.TimeUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.kunpark.resource.services.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by BM Anderson on 04/07/2022.
 */
class OrganizationViewModel: BaseViewModel() {
    private var repository = OrganizationRepository()
    private var _organization: MutableSharedFlow<ArrayList<PersonData>?> = MutableSharedFlow()
    val organization = _organization.asSharedFlow()

    private var _hasChangeOr: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val hasChangeOr = _hasChangeOr.asStateFlow()

    var listPersonDataTab1: ArrayList<PersonData> = arrayListOf()
    var listPersonDataTab2: ArrayList<PersonData> = arrayListOf()

    private var _hasDoneClick1: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    var hasDoneClick1 = _hasDoneClick1.asStateFlow()

    private var _hasDoneClick2: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    var hasDoneClick2 = _hasDoneClick2.asStateFlow()

    private var _selectedClick: MutableStateFlow<ArrayList<PersonData>?> = MutableStateFlow(null)
    var selectedClick = _selectedClick.asStateFlow()


    fun updateDoneClick1(isClick: Boolean?) = viewModelScope.launch {
        _hasDoneClick1.emit(isClick)
    }

    fun updateDoneClick2(isClick: Boolean?) = viewModelScope.launch {
        _hasDoneClick2.emit(isClick)
    }

    fun updateSelected(selected: ArrayList<PersonData>?) = viewModelScope.launch {
        _selectedClick.emit(selected)
    }

    var getOrganizationLiveData : MutableLiveData<Boolean> = MutableLiveData()

    @SuppressLint("SimpleDateFormat")
    fun getDepartmentsMod(params: JsonObject) = viewModelScope.launch(
        Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            getOrganizationLiveData.value = false
        }

        when (val result = repository.getDepartmentMod(params)) {
            is Result.Success -> {
                val body: LinkedTreeMap<String, Any> =
                    result.data.response as LinkedTreeMap<String, Any>
                val success = body["success"] as Double
                if (success == 1.0) {
                    val data: ArrayList<PersonData> = body["data"] as ArrayList<PersonData>

                    if(!data.isNullOrEmpty()) {
                        _hasChangeOr.emit(true)
                        val params2 = JsonObject()
                        params2.addProperty("sessionId", DaZoneApplication.getInstance().prefs.getaccesstoken())
                        params2.addProperty("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes())
                        params2.addProperty("languageCode", Locale.getDefault().language.uppercase(Locale.getDefault()))
                        getDepartments(params2)
                    } else {
                        val params3 = JsonObject()
                        params3.addProperty("sessionId", DaZoneApplication.getInstance().prefs.getaccesstoken())
                        params3.addProperty("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes())
                        params3.addProperty("languageCode", Locale.getDefault().language.uppercase(Locale.getDefault()))
                        params3.addProperty("moddate", SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(Date(System.currentTimeMillis()))) //2022-06-29 10:43:23.286

                        getMemberMod(params3)
                    }

                } else {
                    val error: LinkedTreeMap<String, Any> =
                        body["error"] as LinkedTreeMap<String, Any>
                    val message = error["message"] as String
                    errorMessage.postValue(message)
                }
            }

            is Result.Error -> {
                errorMessage.postValue(result.exception)
            }
        }
    }

    private fun getMemberMod(params: JsonObject) = viewModelScope.launch(
        Dispatchers.IO) {
        when (val result = repository.getMemberMod(params)) {
            is Result.Success -> {
                val body: LinkedTreeMap<String, Any> =
                    result.data.response as LinkedTreeMap<String, Any>
                val success = body["success"] as Double
                if (success == 1.0) {
                    val data: ArrayList<PersonData> = body["data"] as ArrayList<PersonData>

                    if(!data.isNullOrEmpty()) {
                        _hasChangeOr.emit(true)
                        val params2 = JsonObject()
                        params2.addProperty("sessionId", DaZoneApplication.getInstance().prefs.getaccesstoken())
                        params2.addProperty("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes())
                        params2.addProperty("languageCode", Locale.getDefault().language.uppercase(Locale.getDefault()))
                        getDepartments(params2)
                    }

                } else {
                    val error: LinkedTreeMap<String, Any> =
                        body["error"] as LinkedTreeMap<String, Any>
                    val message = error["message"] as String
                    errorMessage.postValue(message)
                }
            }

            is Result.Error -> {
                errorMessage.postValue(result.exception)
            }
        }

    }

    fun getDepartments(params: JsonObject) = viewModelScope.launch(
        Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            getOrganizationLiveData.value = false
        }
        when (val result = repository.getDepartments(params)) {
            is Result.Success -> {
                val body: LinkedTreeMap<String, Any> =
                    result.data.response as LinkedTreeMap<String, Any>
                val success = body["success"] as Double
                if (success == 1.0) {
                    val data: ArrayList<PersonData> = body["data"] as ArrayList<PersonData>
                    val gson = Gson()
                    val json = gson.toJson(data)

                    val list = gson.fromJson<ArrayList<PersonData>>(
                        json,
                        object : TypeToken<ArrayList<PersonData>>() {}.type
                    )
                    if(!list.isNullOrEmpty()) {
                        getChildData(list)
                    }

                } else {
                    val error: LinkedTreeMap<String, Any> =
                        body["error"] as LinkedTreeMap<String, Any>
                    val message = error["message"] as String
                    errorMessage.postValue(message)
                }
            }

            is Result.Error -> {
                errorMessage.postValue(result.exception)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private suspend fun getChildData(data: ArrayList<PersonData>) {
        val job = viewModelScope.launch(Dispatchers.IO) {
            data.forEach {
                Log.d("TIMMI", "forEach with ${it.realDepartNo} and thread is ${Thread.currentThread().id}")
                getChild(it, data)

                it.personList.forEach { child ->
                    withContext(Dispatchers.IO) {
                        checkChild(child, data)
                    }

                }
            }
        }

        job.join()
        Log.d("TIMMI", "DONE with ${data.size}")

        _hasChangeOr.emit(false)

        Prefs().putListOrganization(data)
       _organization.emit(data)
        withContext(Dispatchers.Main) {
            getOrganizationLiveData.value = true
        }
    }

    private suspend fun checkChild(organization: PersonData, data: ArrayList<PersonData>) {
        Log.d("TIMMI", "checkChild with ${organization.realDepartNo} and thread is ${Thread.currentThread().id}")
        getChild(organization, data)

        organization.personList.forEach { child ->
            withContext(Dispatchers.IO) {
                checkChild(child, data)
            }
        }
    }

    fun reSetMod() = viewModelScope.launch {
        _hasChangeOr.emit(null)
    }

    private suspend fun getChild(organization: PersonData, listOriginal: ArrayList<PersonData>) {
        Log.d("TIMMI", "getChild with ${organization.realDepartNo} and thread is ${Thread.currentThread().id}")
        val params = JsonObject()

        params.addProperty("sessionId", DaZoneApplication.getInstance().prefs.accessToken)
        params.addProperty("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes().toString())
        params.addProperty("languageCode", Locale.getDefault().language)
        params.addProperty("departNo", organization.realDepartNo)
        when (val result = repository.getUserByDepartmentNo(params)) {
            is Result.Success -> {
                val body: LinkedTreeMap<String, Any> =
                    result.data.response as LinkedTreeMap<String, Any>
                val success = body["success"] as Double
                if (success == 1.0) {
                    val data: ArrayList<PersonData> = body["data"] as ArrayList<PersonData>

                    val gson = Gson()
                    val json = gson.toJson(data)

                    val list = gson.fromJson<ArrayList<PersonData>>(
                        json,
                        object : TypeToken<ArrayList<PersonData>>() {}.type
                    )

                    if(!list.isNullOrEmpty()) {
                        withContext(Dispatchers.IO) {
                            Log.d("TIMMI", "Has member with departNo = ${organization.departNo} and thread is ${Thread.currentThread().id}")
                            organization.listMembers = ArrayList(list)
                            updateMembers(organization, listOriginal)
                        }

                    }

                } else {
                    val error: LinkedTreeMap<String, Any> =
                        body["error"] as LinkedTreeMap<String, Any>
                    val message = error["message"] as String
                    errorMessage.postValue(message)
                }
            }

            is Result.Error -> {
                errorMessage.postValue(result.exception)
            }
        }
    }

    private fun updateMembers(organization: PersonData, data: ArrayList<PersonData>)  {
        val iterator = data.iterator()
        while (iterator.hasNext()) {
            Log.d("TIMMI", "updateMembers with ${organization.realDepartNo} and thread is ${Thread.currentThread().id}")
            val it = iterator.next()
            if(it.realDepartNo == organization.realDepartNo) {
                it.listMembers = ArrayList(organization.listMembers)
            } else if(!it.personList.isNullOrEmpty()) {
                updateMembers(organization, it.personList)
            }
        }

    }

    fun saveListPersonDataTab1(listNew: ArrayList<PersonData>) {
        listPersonDataTab1.clear()
        listPersonDataTab1.addAll(listNew)
        Log.d("SAVELIST1", "${listNew.size}")
    }

    fun saveListPersonDataTab2(listNew: ArrayList<PersonData>) {
        listPersonDataTab2.clear()
        listPersonDataTab2.addAll(listNew)
        Log.d("SAVELIST2", "${listNew.size}")
    }
}