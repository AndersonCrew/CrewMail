package com.dazone.crewemail.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.dazone.crewemail.R
import com.dazone.crewemail.activities.new_refactor.OrganizationViewModel
import com.dazone.crewemail.adapter.AdapterOrganizationPager
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.ActivityOrganizationNewBinding
import com.dazone.crewemail.utils.Constants
import com.dazone.crewemail.utils.Prefs
import com.dazone.crewemail.utils.StaticsBundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.collections.ArrayList


/**
 * Created by BM Anderson on 04/07/2022.
 */
@OptIn(DelicateCoroutinesApi::class)
class OrganizationChartActivity : BaseActivity() {
    private var binding: ActivityOrganizationNewBinding? = null
    private var adapter: AdapterOrganizationPager? = null
    private var data: ArrayList<PersonData> = arrayListOf()
    private var viewModel: OrganizationViewModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOrganizationNewBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(OrganizationViewModel::class.java)
        setContentView(binding?.root)
        initView()
        super.onCreate(savedInstanceState)
    }

    private fun initView() {
        viewModel?.selectedClick?.onEach {
            if(!it.isNullOrEmpty()) {
                resetChecked(it)
                viewModel?.updateSelected(null)
            }
        }?.launchIn(lifecycleScope)

        getIntentSelected()

        binding?.tvCompany?.setOnClickListener {
            changeTab(0)
        }

        binding?.tvSearch?.setOnClickListener {
            changeTab(1)
        }

        binding?.imgBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.imgDone?.setOnClickListener {
            GlobalScope.launch {
                if(binding?.vpOrganization?.currentItem == 0) {
                    viewModel?.updateDoneClick1(true)
                } else {
                    viewModel?.updateDoneClick2(true)
                }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch {
            resetChecked(null)
        }
    }

    private suspend fun resetChecked(selected: ArrayList<PersonData>?) {
        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch {
            data.forEach {
                Log.d("RECHECK", "resetChecked ${Thread.currentThread()}")
                it.isChosen = false
                it.listMembers.forEach { member -> member.isChosen = false }

                it.personList.forEach { department ->
                    withContext(Dispatchers.IO) {
                       reSetDepartment(department)
                    }
                }
            }
        }

        job.join()
        Log.d("RECHECK", "resetChecked DONE")
        Prefs().putListOrganization(data)


        if(!selected.isNullOrEmpty()) {
            val intent = Intent()
            intent.putExtra(StaticsBundle.BUNDLE_LIST_PERSON, Gson().toJson(selected))
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private suspend fun reSetDepartment(personData: PersonData) {
        Log.d("RECHECK", "reSetDepartment ${Thread.currentThread()}")
        personData.isChosen = false
        personData.listMembers.forEach { member -> member.isChosen = false }

        personData.personList.forEach { department ->
            withContext(Dispatchers.IO) {
                reSetDepartment(department)
            }
        }
    }

    private fun initViewPager() {
        Log.d("RECHECK", "initViewPager")
        Prefs().putListOrganization(data)
        adapter = AdapterOrganizationPager(supportFragmentManager)

        binding?.vpOrganization?.adapter = adapter
        changeTab( binding?.vpOrganization?.currentItem ?: 0)
        binding?.vpOrganization?.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (i != tabSelected) {
                    changeTab(i)
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    private fun getIntentSelected() = GlobalScope.launch {
        Log.d("RECHECK", "getIntentSelected")
        Prefs().listOrganization?.let {
            data = ArrayList(it)
            intent.getStringExtra(Constants.SELECTED_LIST)?.let {
                val selects = Gson().fromJson<ArrayList<PersonData>>(
                    intent.getStringExtra(Constants.SELECTED_LIST),
                    object : TypeToken<ArrayList<PersonData?>?>() {}.type
                )

                Log.d("RECHECK", "getIntentSelected size selected = ${selects.size}")
                if (!selects.isNullOrEmpty()) {
                    //TODO Update list
                    val scope = CoroutineScope(Dispatchers.IO)
                    val job = scope.launch {
                        selects.forEach {
                            Log.d("RECHECK", "getIntentSelected forEach")
                            updateSelected(it, data)

                        }
                    }

                    job.join()
                    Prefs().putListOrganization(data)
                    withContext(Dispatchers.Main) {
                        initViewPager()
                    }
                } else if(!data.isNullOrEmpty()) {
                    withContext(Dispatchers.Main) {
                        initViewPager()
                    }
                }
            }
        }

    }

    private fun updateSelected(personData: PersonData, listCheck: ArrayList<PersonData>) {
        listCheck.forEach {
            Log.d("RECHECK", "updateSelected forEach ${listCheck.size}")
            if(!it.listMembers.filter { member -> member.getmEmail() == personData.getmEmail() }.isNullOrEmpty()) {
                Log.d("RECHECK", "Has Member ${it.listMembers.filter { member -> member.getmEmail() == personData.getmEmail() }.size}")
                val iterator = it.listMembers.filter { member -> member.getmEmail() == personData.getmEmail() }.iterator()
                while (iterator.hasNext()) {
                    var member = iterator.next()
                    member.isChosen = true
                }
            }

            if(!it.personList.isNullOrEmpty()) {
                Log.d("RECHECK", "updateSelected PersonList ${it.personList.size}")
                updateSelected(personData, it.personList)
            }
        }
    }

    private var tabSelected = -1
    private fun changeTab(tab: Int) {
        tabSelected = tab
        if (tab == 0) {
            binding?.tvCompany?.setBackgroundResource(R.drawable.bg_selected)
            binding?.tvCompany?.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding?.tvSearch?.setBackgroundResource(R.drawable.bg_non_selected)
            binding?.tvSearch?.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        } else {
            binding?.tvCompany?.setBackgroundResource(R.drawable.bg_non_selected)
            binding?.tvSearch?.setBackgroundResource(R.drawable.bg_selected)
            binding?.tvCompany?.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding?.tvSearch?.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding?.vpOrganization?.currentItem = tab
        Log.d("ANDERSON", "tab = $tab")
    }
}