package com.shadi.assignment.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shadi.assignment.R
import com.shadi.assignment.domain.model.Status
import com.shadi.assignment.domain.model.UserProfile

class MatchAdapter(
    private val onAccept: (String) -> Unit,
    private val onDecline: (String) -> Unit
) : PagingDataAdapter<UserProfile, MatchAdapter.MatchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match_card, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val profile = getItem(position)
        profile?.let { holder.bind(it, onAccept, onDecline) }
    }

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvGender: TextView = itemView.findViewById(R.id.tvGender)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnDecline: Button = itemView.findViewById(R.id.btnDecline)
        private val layoutAction: View = itemView.findViewById(R.id.layoutAction)
        private val tvStatusAction: TextView = itemView.findViewById(R.id.tvStatusAction)

        fun bind(profile: UserProfile, onAccept: (String) -> Unit, onDecline: (String) -> Unit) {
            val context = itemView.context
            tvName.text = context.getString(R.string.profile_name, profile.firstName, profile.lastName)
            tvLocation.text = context.getString(R.string.profile_location, profile.city, profile.country)
            tvEmail.text = profile.email
            tvGender.text = profile.gender
            ivProfile.contentDescription = context.getString(R.string.profile_image_desc, profile.firstName, profile.lastName)
            Glide.with(ivProfile.context).load(profile.imageUrl).into(ivProfile)
            btnAccept.text = context.getString(R.string.accept)
            btnDecline.text = context.getString(R.string.decline)
            btnAccept.setOnClickListener { onAccept(profile.uuid) }
            btnDecline.setOnClickListener { onDecline(profile.uuid) }

            when (profile.status) {
                Status.ACCEPTED -> {
                    layoutAction.visibility = View.GONE
                    tvStatusAction.visibility = View.VISIBLE
                    tvStatusAction.text = context.getString(R.string.member_accepted)
                    tvStatusAction.setBackgroundResource(R.drawable.bg_status_accepted)
                    tvStatusAction.setTextColor(context.getColor(android.R.color.white))
                }
                Status.DECLINED -> {
                    layoutAction.visibility = View.GONE
                    tvStatusAction.visibility = View.VISIBLE
                    tvStatusAction.text = context.getString(R.string.member_declined)
                    tvStatusAction.setBackgroundResource(R.drawable.bg_status_declined)
                    tvStatusAction.setTextColor(context.getColor(android.R.color.white))
                }
                else -> {
                    layoutAction.visibility = View.VISIBLE
                    tvStatusAction.visibility = View.GONE
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile) = oldItem.uuid == newItem.uuid
        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile) = oldItem == newItem
    }
}
