package com.dazone.crewemail.activities.new_refactor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.ItemSearchMemberBinding


/**
 * Created by BM Anderson on 05/07/2022.
 */
class SearchMemberAdapter: ListAdapter<PersonData, SearchMemberAdapter.SearchMemberViewHolder>(
    object : DiffUtil.ItemCallback<PersonData>() {
        override fun areItemsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem.isChosen == newItem.isChosen
        }

        override fun areContentsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem.fullName == newItem.fullName
        }
    }
) {

    class SearchMemberViewHolder(val binding: ItemSearchMemberBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMemberViewHolder {
        val binding = ItemSearchMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMemberViewHolder, position: Int) {

    }
}