package com.example.githubdemo.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 个人资料ViewModel工厂
 */
class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val gitHubRepository: GitHubRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepository, gitHubRepository) as T
        }
        throw IllegalArgumentException("未知的ViewModel类: ${modelClass.name}")
    }
} 