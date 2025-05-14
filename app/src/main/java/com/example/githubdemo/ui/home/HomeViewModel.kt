package com.example.githubdemo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.model.Topic
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 首页ViewModel
 */
class HomeViewModel(private val gitHubRepository: GitHubRepository) : ViewModel() {

    // 热门仓库数据
    val trendingRepositories: Flow<List<Repository>> = gitHubRepository.getTrendingRepositories()
    
    // 热门话题数据
    val featuredTopics: Flow<List<Topic>> = gitHubRepository.getFeaturedTopics()
    
    // 加载状态
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    
    // 错误状态
    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error
    
    init {
        loadData()
    }
    
    /**
     * 加载数据
     */
    fun loadData() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = false
            try {
                gitHubRepository.refreshHomeData()
                _loading.value = false
            } catch (e: Exception) {
                _error.value = true
                _loading.value = false
            }
        }
    }
} 