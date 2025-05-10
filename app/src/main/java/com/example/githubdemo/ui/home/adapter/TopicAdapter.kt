package com.example.githubdemo.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.data.model.Topic
import com.example.githubdemo.databinding.ItemTopicBinding

/**
 * 话题适配器
 */
class TopicAdapter : ListAdapter<Topic, TopicAdapter.ViewHolder>(TopicDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = getItem(position)
        holder.bind(topic)
    }

    inner class ViewHolder(private val binding: ItemTopicBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.apply {
                topicNameTextView.text = topic.displayName ?: topic.name
                topicDescriptionTextView.text = topic.description ?: topic.shortDescription ?: ""
                
                if (topic.createdBy != null) {
                    createdByContainer.visibility = View.VISIBLE
                    createdByTextView.text = topic.createdBy
                } else {
                    createdByContainer.visibility = View.GONE
                }
                
                if (topic.released != null) {
                    releasedContainer.visibility = View.VISIBLE
                    releasedTextView.text = topic.released
                } else {
                    releasedContainer.visibility = View.GONE
                }
            }
        }
    }
}

/**
 * 话题差异回调
 */
class TopicDiffCallback : DiffUtil.ItemCallback<Topic>() {
    override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
        return oldItem == newItem
    }
} 