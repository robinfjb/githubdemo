package com.example.githubdemo.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 搜索ViewModel工厂
 */
class SearchViewModelFactory(
    private val gitHubRepository: GitHubRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(gitHubRepository) as T
        }
        throw IllegalArgumentException("未知的ViewModel类: ${modelClass.name}")
    }
} 