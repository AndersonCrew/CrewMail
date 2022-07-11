package com.dazone.crewemail.activities.new_refactor

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.ItemOrganizationNewBinding
import kotlinx.coroutines.*

/**
 * Created by BM Anderson on 02/07/2022.
 */
class OrganizationAdapter(private val onCheckedDone: (PersonData, Boolean) -> Unit, private val onMemberCheckedChange: (PersonData?, PersonData, Boolean) -> Unit):
    ListAdapter<PersonData, OrganizationAdapter.OrganizationViewHolder>(
    object : DiffUtil.ItemCallback<PersonData>() {
        override fun areItemsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem.realDepartNo == newItem.realDepartNo
        }

        override fun areContentsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem == newItem
        }
    }
) {

    class OrganizationViewHolder(val binding: ItemOrganizationNewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizationViewHolder {
        val binding = ItemOrganizationNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrganizationViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: OrganizationViewHolder, position: Int) {
        val organization = getItem(position)

        holder.binding.tvTitle.text = organization.fullName
        holder.binding.icCheck.isChecked = organization.isChosen

        if(!organization.personList.isNullOrEmpty()) {
            val departmentAdapter = OrganizationAdapter({ organization, isChecked ->
                onCheckedDone.invoke(organization, isChecked)
            }, { department, member, isChecked ->
                onMemberCheckedChange.invoke(department, member, isChecked)
            })

            holder.binding.rvChildOrganization.adapter = departmentAdapter
            departmentAdapter.submitList(organization.personList)
        }

        if(!organization.listMembers.isNullOrEmpty()) {
            val departmentAdapter = AdapterMember(organization) { department, member, isChecked ->
                onMemberCheckedChange.invoke(department, member, isChecked)
            }

            holder.binding.rvMembers.adapter = departmentAdapter
            departmentAdapter.submitList(organization.listMembers)
        }

        holder.binding.icCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked != organization.isChosen) {
                onCheckedDone.invoke(getItem(position), isChecked)
            }
        }

        holder.binding.imgFolder.setOnClickListener {
            if(holder.binding.rvMembers.visibility == View.GONE) {
                holder.binding.rvMembers.visibility = View.VISIBLE
                holder.binding.rvChildOrganization.visibility = View.VISIBLE
            } else {
                holder.binding.rvMembers.visibility = View.GONE
                holder.binding.rvChildOrganization.visibility = View.GONE
            }
        }
    }

    suspend fun getSelected(): ArrayList<PersonData> {
        val scope = CoroutineScope(Dispatchers.IO)
        var selected: ArrayList<PersonData> = arrayListOf()
        val job = scope.launch {
            for(i in 0 until itemCount) {
                Log.d("RECHECK", "getSelected for i = $i")
                val personData: PersonData = getItem(i)

                selected.addAll(personData.listMembers.filter { it.isChosen })

                if(!personData.personList.isNullOrEmpty()) {
                    withContext(Dispatchers.IO) {
                        checkDepartment(personData.personList, selected)
                    }

                }
            }
        }

        job.join()
        Log.d("RECHECK", "getSelected DONE size = ${selected.size}")
        val result : ArrayList<PersonData> = ArrayList(selected)
        return result
    }

    private suspend fun checkDepartment(departments: ArrayList<PersonData>, selected: ArrayList<PersonData>) {
        departments.forEach {
            Log.d("RECHECK", "checkDepartment forEach size = ${departments.size}")
            selected.addAll(it.listMembers.filter { member -> member.isChosen })

            if(!it.personList.isNullOrEmpty()) {
                withContext(Dispatchers.IO) {
                    checkDepartment(it.personList, selected)
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun checkChange(checked: Boolean, personData: PersonData, position: Int) {
        Log.d("CHANGE", "START CHECK")
        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch {
            personData.isChosen = checked
            if(!personData.listMembers.isNullOrEmpty()) {
                val iterator = personData.listMembers.iterator()
                while(iterator.hasNext()) {
                    iterator.next().apply {
                        isChosen = checked
                        Log.d("CHANGE", "Check member $fullName")
                    }
                }
            }

            if(!personData.personList.isNullOrEmpty()) {
                Log.d("CHANGE", "Has Department")
                withContext(Dispatchers.IO) {
                    personData.personList.forEach { child ->
                        checkChildChange(checked, child)
                    }
                }
            }
        }

        job.join()

        withContext(Dispatchers.Main) {
            Log.d("CHANGE", "DONE")
            //onCheckedDone.invoke()
            notifyItemChanged(position)
        }
    }

    private suspend fun checkChildChange(checked: Boolean, personData: PersonData) {
        Log.d("CHANGE", "checkChildChange ${personData.fullName}")
        if(!personData.listMembers.isNullOrEmpty()) {
            val iterator = personData.listMembers.iterator()
            while(iterator.hasNext()) {
                iterator.next().apply {
                    Log.d("CHANGE", "checkChildChange has member $fullName")
                    isChosen = checked
                }
            }
        }

        if(!personData.personList.isNullOrEmpty()) {
            withContext(Dispatchers.IO) {
                personData.personList.forEach { child ->
                    checkChildChange(checked, child)
                }
            }
        }
    }
}