package com.example.githubdemo.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 创建问题ViewModel工厂
 */
class CreateIssueViewModelFactory(
    private val ownerLogin: String,
    private val repoName: String,
    private val gitHubRepository: GitHubRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateIssueViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateIssueViewModel(ownerLogin, repoName, gitHubRepository) as T
        }
        throw IllegalArgumentException("未知的ViewModel类: ${modelClass.name}")
    }
} 