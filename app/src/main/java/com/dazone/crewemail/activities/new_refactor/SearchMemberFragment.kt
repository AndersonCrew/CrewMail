package com.dazone.crewemail.activities.new_refactor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.FragmentSearchRenewBinding
import com.dazone.crewemail.fragments.BaseFragment
import com.dazone.crewemail.utils.Constants
import com.dazone.crewemail.utils.Prefs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by BM Anderson on 05/07/2022.
 */
class SearchMemberFragment: BaseFragment() {
    private var binding: FragmentSearchRenewBinding?= null
    private var viewModel: OrganizationViewModel?= null
    private var list: ArrayList<PersonData> = arrayListOf()
    private var adapter: AdapterMember?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchRenewBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity()).get(OrganizationViewModel::class.java)
        GlobalScope.launch {
            initView()
        }
        return binding?.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun initView() {

        viewModel?.hasDoneClick2?.onEach {
            if(it != null && it) {
                viewModel?.updateSelected(selected)
                viewModel?.updateDoneClick2(null)
            }
        }?.launchIn(lifecycleScope)

        binding?.imgSearch?.setOnClickListener { v: View? ->
            if (!binding?.etSearch?.text.toString().isEmpty()) {
                actionSearch()
            }
        }

        binding?.etSearch?.setOnEditorActionListener { v: TextView?, actionId: Int, event: android.view.KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!binding?.etSearch?.text.toString().isEmpty()) {
                    actionSearch()
                }

                true
            }
            false
        }

        binding?.etSearch?.addTextChangedListener {
            if(binding?.etSearch?.text.isNullOrEmpty()) {
                adapter?.submitList(list)
            }
        }

        val data: ArrayList<PersonData> = Prefs().listOrganization

        val scope = CoroutineScope(Dispatchers.IO)
        val job = scope.launch {
            list.clear()
            selected.clear()
            if (!data.isNullOrEmpty()) {
                data.forEach {
                    if (!it.listMembers.isNullOrEmpty()) {
                        it.listMembers.forEach { member ->
                            if(!list.contains(member)) {
                                list.add(member)
                            }
                        }
                    }


                    withContext(Dispatchers.IO) {
                        checkChild(it.personList)
                    }

                }
            }
        }

        job.join()
        selected = ArrayList(list.filter { it.isChosen })
        initRecyclerView(list)
    }

    private fun initRecyclerView(list: ArrayList<PersonData>) {
        adapter = AdapterMember(null) { _, member, isChecked ->
            if(isChecked) {
                if(selected.find { it.userNo == member.userNo } == null) {
                    selected.add(member)
                }
            } else {
                selected.find { it.userNo == member.userNo }?.let {
                    selected.remove(member)
                }

            }
        }

        binding?.rvMembers?.adapter = adapter
        adapter?.submitList(list)
    }

    private var listSearch = ArrayList<PersonData>()
    private fun actionSearch() {
        hideKeyBoard()
        listSearch = arrayListOf()
        val keyword: String = binding?.etSearch?.text.toString().lowercase(Locale.getDefault())
        for (personData in list) {
            if (personData.fullName.lowercase(Locale.getDefault()).contains(keyword)) {
                listSearch.add(personData)
            }
        }

        adapter?.submitList(listSearch)
    }

    private fun hideKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding?.etSearch?.windowToken, 0)
    }

    private suspend fun checkChild(personList: ArrayList<PersonData>?) {
        personList?.forEach {
            withContext(Dispatchers.IO) {
                if (!it.listMembers.isNullOrEmpty()) {
                    it.listMembers.forEach { member ->
                        if(!list.contains(member)) {
                            list.add(member)
                        }
                    }
                }

                checkChild(it.personList)
            }
        }
    }

    private var selected: ArrayList<PersonData> = arrayListOf()
}