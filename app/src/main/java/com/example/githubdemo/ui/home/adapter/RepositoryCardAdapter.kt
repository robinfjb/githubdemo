package com.example.githubdemo.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubdemo.R
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.databinding.ItemRepositoryCardBinding

/**
 * 仓库卡片适配器
 */
class RepositoryCardAdapter(
    private val onItemClick: (Repository) -> Unit
) : ListAdapter<Repository, RepositoryCardAdapter.ViewHolder>(RepositoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepositoryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = getItem(position)
        holder.bind(repository)
    }

    inner class ViewHolder(private val binding: ItemRepositoryCardBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(repository: Repository) {
            binding.apply {
                repoNameTextView.text = repository.name
                repoDescriptionTextView.text = repository.description ?: 
                    binding.root.context.getString(R.string.no_description)
                ownerNameTextView.text = repository.owner.login
                
                // 设置语言标签
                if (repository.language != null) {
                    languageChip.text = repository.language
                    languageChip.visibility = android.view.View.VISIBLE
                } else {
                    languageChip.visibility = android.view.View.GONE
                }
                
                // 加载头像
                Glide.with(ownerAvatarImageView.context)
                    .load(repository.owner.avatarUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ownerAvatarImageView)
            }
        }
    }
}

/**
 * 仓库差异回调
 */
class RepositoryDiffCallback : DiffUtil.ItemCallback<Repository>() {
    override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean {
        return oldItem == newItem
    }
} 