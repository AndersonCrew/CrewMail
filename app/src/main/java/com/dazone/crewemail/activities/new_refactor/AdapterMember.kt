package com.dazone.crewemail.activities.new_refactor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazone.crewemail.DaZoneApplication
import com.dazone.crewemail.data.PersonData
import com.dazone.crewemail.databinding.ItemMemberNewBinding

/**
 * Created by BM Anderson on 03/07/2022.
 */
class AdapterMember(private val department: PersonData?, private val onCheckedChanged: (PersonData?, PersonData, Boolean) -> Unit): ListAdapter<PersonData, AdapterMember.MemberViewHolder>(
    object : DiffUtil.ItemCallback<PersonData>() {
        override fun areItemsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem.userNo == newItem.userNo
        }

        override fun areContentsTheSame(oldItem: PersonData, newItem: PersonData): Boolean {
            return oldItem == newItem
        }
    }
) {

    class MemberViewHolder(val binding: ItemMemberNewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val personData = getItem(position)

        holder.binding.tvName.text = personData.fullName
        holder.binding.tvPosition.text = personData.positionName
        val urlAvatar = DaZoneApplication.getInstance().prefs.serverSite + personData.urlAvatar

        holder.binding.ckChoose.setOnCheckedChangeListener(null)
        holder.binding.ckChoose.isChecked = personData.isChosen
        holder.binding.ckChoose.setOnCheckedChangeListener { compoundButton, b ->
            if(b != personData.isChosen ) {
                if(department == null) {
                    personData.isChosen = b
                }

                onCheckedChanged.invoke(department,  getItem(position), b)
            }
        }


        var requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide
            .with(holder.itemView.context)
            .load(urlAvatar)
            .apply(requestOptions)
            .into(holder.binding.imgAvatar)
    }
}