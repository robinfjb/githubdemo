package com.example.githubdemo.ui.repository.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.databinding.ItemIssueBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 问题适配器
 */
class IssueAdapter(
    private val onIssueClick: (Issue) -> Unit = {}
) : ListAdapter<Issue, IssueAdapter.ViewHolder>(IssueDiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = getItem(position)
        holder.bind(issue)
    }

    inner class ViewHolder(private val binding: ItemIssueBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(issue: Issue) {
            binding.apply {
                issueTitleTextView.text = issue.title
                issueBodyTextView.text = issue.body
                
                issueNumberTextView.text = "#${issue.number}"
                
                // 格式化创建时间
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        .parse(issue.createdAt)
                    issueDateTextView.text = date?.let { dateFormat.format(it) } ?: issue.createdAt
                } catch (e: Exception) {
                    issueDateTextView.text = issue.createdAt
                }
                
                // 设置作者名
                authorTextView.text = issue.user.login
                
                // 设置点击事件
                root.setOnClickListener {
                    onIssueClick(issue)
                }
            }
        }
    }
}

/**
 * 问题差异回调
 */
class IssueDiffCallback : DiffUtil.ItemCallback<Issue>() {
    override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean {
        return oldItem == newItem
    }
} 