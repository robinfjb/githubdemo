package com.example.githubdemo.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 仓库详情ViewModel工厂
 */
class RepositoryDetailViewModelFactory(
    private val ownerLogin: String,
    private val repoName: String,
    private val gitHubRepository: GitHubRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepositoryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RepositoryDetailViewModel(
                ownerLogin,
                repoName,
                gitHubRepository,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("未知的ViewModel类: ${modelClass.name}")
    }
} 