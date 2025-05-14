package com.example.githubdemo.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 搜索ViewModel
 */
class SearchViewModel(private val gitHubRepository: GitHubRepository) : ViewModel() {

    // 搜索结果
    private val _searchResults = MutableStateFlow<List<Repository>>(emptyList())
    val searchResults: StateFlow<List<Repository>> = _searchResults
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 是否还有更多数据
    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData
    
    // 当前页码
    private var currentPage = 1
    
    // 当前搜索查询
    private var currentQuery = ""
    
    // 当前搜索的语言
    private var currentLanguage = ""
    
    /**
     * 搜索仓库
     */
    fun searchRepositories(query: String) {
        // 重置分页状态
        currentPage = 1
        currentQuery = query
        _searchResults.value = emptyList()
        _hasMoreData.value = true
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val finalQuery = if(currentLanguage.isNotEmpty()) {
                    query.plus(" language:$currentLanguage")
                } else {
                    query
                }
                val resultsFlow = gitHubRepository.searchRepositories(finalQuery, page = currentPage)
                resultsFlow.collectLatest { repos ->
                    _searchResults.value = repos
                    _isLoading.value = false
                    // 如果返回的结果少于每页数量，则认为没有更多数据
                    _hasMoreData.value = repos.size >= 30
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _isLoading.value = false
                _hasMoreData.value = false
            }
        }
    }
    
    /**
     * 按语言搜索
     */
    fun searchByLanguage(language: String) {
        currentLanguage = language
        if(currentQuery.isEmpty()) {
            return
        }
        searchRepositories(currentQuery)
    }
    
    /**
     * 加载更多数据
     */
    fun loadMoreResults() {
        if (!_hasMoreData.value || _isLoading.value) {
            return
        }
        
        currentPage++
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val finalQuery = if(currentLanguage.isNotEmpty()) {
                    currentQuery.plus(" language:$currentLanguage")
                } else {
                    currentQuery
                }
                val resultsFlow = gitHubRepository.searchRepositories(finalQuery, page = currentPage)
                resultsFlow.collectLatest { newRepos ->
                    val currentList = _searchResults.value
                    val updatedList = currentList + newRepos
                    _searchResults.value = updatedList
                    _isLoading.value = false
                    // 如果返回的结果少于每页数量，则认为没有更多数据
                    _hasMoreData.value = newRepos.isNotEmpty() && newRepos.size >= 30
                }
            } catch (e: Exception) {
                _isLoading.value = false
                // 加载更多失败时，不清空现有结果，只是标记没有更多数据
                _hasMoreData.value = false
            }
        }
    }
    
    /**
     * 清除搜索结果
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        currentPage = 1
        currentQuery = ""
        currentLanguage = ""
        _hasMoreData.value = true
    }
} 