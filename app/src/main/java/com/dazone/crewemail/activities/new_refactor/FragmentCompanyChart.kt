package com.dazone.crewemail.activities.new_refactor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.FragmentCompanyRenewBinding
import com.dazone.crewemail.fragments.BaseFragment
import com.dazone.crewemail.utils.Constants
import com.dazone.crewemail.utils.Prefs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.internal.wait

/**
 * Created by BM Anderson on 05/07/2022.
 */

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("NotifyDataSetChanged")
class FragmentCompanyChart : BaseFragment() {
    private var adapter: OrganizationAdapter? = null
    private var binding: FragmentCompanyRenewBinding? = null
    private var viewModel: OrganizationViewModel? = null
    private var data: ArrayList<PersonData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompanyRenewBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity()).get(OrganizationViewModel::class.java)
        initView()
        return binding?.root
    }

    private fun initView() {
        data = arrayListOf()
        data.addAll(Prefs().listOrganization)
        if (!data.isNullOrEmpty()) {
            initRecyclerView(data)
        }

        viewModel?.hasDoneClick1?.onEach {
            if(it != null && it) {
                viewModel?.updateSelected(selected)
                viewModel?.updateDoneClick1(null)
            }
        }?.launchIn(lifecycleScope)
    }


    private fun initRecyclerView(list: ArrayList<PersonData>?) {
        adapter = OrganizationAdapter({ organization, isChecked ->
            GlobalScope.launch {
                checkOrganization(organization, isChecked)
            }
        }, { department, member, isChecked ->
            GlobalScope.launch {
                if(department != null) {
                    checkMember(department, member, isChecked)
                }
            }
        })

        binding?.rvOrganization?.adapter = adapter
        adapter?.submitList(list)

        GlobalScope.launch {
            selected.clear()
            val listSelected: Deferred<ArrayList<PersonData>> = async { adapter!!.getSelected() }
            selected.addAll( listSelected.await())
        }
    }

    private val selected: ArrayList<PersonData> = arrayListOf()
    private suspend fun checkOrganization(organization: PersonData, checked: Boolean) {
        Log.d("CHOOSE_DEPARTMENT", "START checkOrganization")
        val job = GlobalScope.launch {
            organization.isChosen = checked

            organization.listMembers.forEach { member ->
                member.isChosen = checked
                if(checked) {
                    if(selected.find { it.userNo == member.userNo } == null) {
                        selected.add(member)
                    }
                } else {
                    selected.find { it.userNo == member.userNo }?.let {
                        selected.remove(member)
                    }

                }


                Log.d("CHOOSE_DEPARTMENT", "Found member ${member.fullName} checkOrganization")
            }

            if(!organization.personList.isNullOrEmpty()) {
                organization.personList.forEach { department ->
                    withContext(Dispatchers.IO) {
                        checkDepartment(checked, department)
                    }
                }
            }
        }

        job.join()
        Log.d("CHOOSE_DEPARTMENT", "DONE checkOrganization")
        updateDataAdapter(organization)
    }

    private suspend fun updateDataAdapter(organization: PersonData) {
        val job = GlobalScope.launch {
            val iterator = data.iterator()
            while (iterator.hasNext()) {
                val department = iterator.next()
                if(organization.realDepartNo == department.realDepartNo) {
                    department.isChosen = organization.isChosen
                    department.listMembers = ArrayList(organization.listMembers)
                    department.personList = ArrayList(organization.personList)

                    Log.d("CHOOSE_DEPARTMENT", "Found Department ${department.realDepartNo} - ${organization.realDepartNo}")
                    return@launch
                } else {
                    if(!department.personList.isNullOrEmpty()) {
                        checkUpdateDepart(organization, department.personList)
                    }
                }
            }
        }

        job.join()
        Log.d("CHOOSE_DEPARTMENT", "DONE updateDataAdapter")
        Log.d("CHOOSE_DEPARTMENT", "Selected size = ${selected.size}")

        withContext(Dispatchers.Main) {
            adapter?.submitList(data)
            adapter?.notifyDataSetChanged()
        }

    }

    private fun checkUpdateDepart(organization: PersonData, personList: ArrayList<PersonData>) {
        Log.d("CHOOSE_DEPARTMENT", "Check child departments")
        val iterator = personList.iterator()
        while (iterator.hasNext()) {
            val department = iterator.next()
            if(organization.realDepartNo == department.realDepartNo) {
                department.isChosen = organization.isChosen
                department.listMembers = ArrayList(organization.listMembers)
                department.personList = ArrayList(organization.personList)

                Log.d("CHOOSE_DEPARTMENT", "Found Department ${department.realDepartNo} - ${organization.realDepartNo}")
                return
            } else {
                checkUpdateDepart(organization, department.personList)
            }
        }
    }

    private suspend fun checkDepartment(checked: Boolean, department: PersonData) {
        Log.d("CHOOSE_DEPARTMENT", "checkDepartment checkOrganization ${department.fullName}")
        department.isChosen = checked

        department.listMembers.forEach { member ->
            member.isChosen = checked
            if(checked) {
                if(selected.find { it.userNo == member.userNo } == null) {
                    selected.add(member)
                }
            } else {
                selected.find { it.userNo == member.userNo }?.let {
                    selected.remove(member)
                }
            }
            Log.d("CHOOSE_DEPARTMENT", "Found member ${member.fullName} checkOrganization")
        }

        if(!department.personList.isNullOrEmpty()) {
            department.personList.forEach { department ->
                withContext(Dispatchers.IO) {
                    checkDepartment(checked, department)
                }
            }
        }
    }

    private suspend fun checkMember(department: PersonData, member: PersonData, isChecked: Boolean) {
        val job = GlobalScope.launch {
            val iterator = data.iterator()
            while (iterator.hasNext()) {
                val organization = iterator.next()
                if(organization.realDepartNo == department.realDepartNo) {
                    Log.d("CHOOSE_DEPARTMENT", "Found Department ${department.realDepartNo} - ${organization.realDepartNo}")
                    if(!organization.listMembers.isNullOrEmpty()) {
                        organization.listMembers.find { it.userNo == member.userNo }?.let { memberChild ->
                            memberChild.isChosen = isChecked
                            if(isChecked) {
                                if(selected.find { it.userNo == member.userNo } == null) {
                                    selected.add(memberChild)
                                }
                            } else {
                                selected.find { it.userNo == member.userNo }?.let {
                                    selected.remove(memberChild)
                                }
                            }
                            Log.d("CHOOSE_DEPARTMENT", "Found Member ${memberChild.userNo} - ${memberChild.isChosen}")
                            return@launch
                        }
                    }
                } else {
                    if(!organization.personList.isNullOrEmpty()) {
                        withContext(Dispatchers.IO) {
                            checkChooseMember(department, member, organization.personList, isChecked)
                        }
                    }
                }
            }
        }

        job.join()
        Log.d("CHOOSE_DEPARTMENT", " Choose member DONE Selected size = ${selected.size}")
        withContext(Dispatchers.Main) {
            adapter?.submitList(data)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun checkChooseMember(department: PersonData, member: PersonData, deparments: ArrayList<PersonData>, isChecked: Boolean) {
        Log.d("CHOOSE_DEPARTMENT", "checkChooseMember")
        val iterator = deparments.iterator()
        while (iterator.hasNext()) {
            val organization = iterator.next()
            if(organization.realDepartNo == department.realDepartNo) {
                Log.d("CHOOSE_DEPARTMENT", "Found Department ${department.realDepartNo} - ${organization.realDepartNo}")
                if(!organization.listMembers.isNullOrEmpty()) {
                    organization.listMembers.find { it.userNo == member.userNo }?.let { memberChild ->
                        memberChild.isChosen = isChecked

                        if(isChecked) {
                            if(selected.find { it.userNo == member.userNo } == null) {
                                selected.add(memberChild)
                            }
                        } else {
                            selected.find { it.userNo == member.userNo }?.let {
                                selected.remove(memberChild)
                            }
                        }
                        Log.d("CHOOSE_DEPARTMENT", "Found Member ${memberChild.userNo} - ${memberChild.isChosen}")
                        return
                    }
                }
            } else {
                if(!organization.personList.isNullOrEmpty()) {
                    checkChooseMember(department, member, organization.personList, isChecked)
                }
            }
        }
    }
}