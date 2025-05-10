package com.example.githubdemo.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.data.model.IssueRequest
import com.example.githubdemo.data.repository.GitHubRepository
import kotlinx.coroutines.launch

/**
 * 创建问题ViewModel
 */
class CreateIssueViewModel(
    private val ownerLogin: String,
    private val repoName: String,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 问题创建状态
    private val _issueCreated = MutableLiveData<Boolean>(false)
    val issueCreated: LiveData<Boolean> = _issueCreated
    
    // 错误消息
    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error
    
    /**
     * 创建问题
     */
    fun createIssue(title: String, body: String) {
        if (title.isEmpty()) {
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            
            try {
                val issueRequest = IssueRequest(title, body)
                val issue = gitHubRepository.createIssue(ownerLogin, repoName, issueRequest)
                _issueCreated.value = true
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "创建问题失败"
                _isLoading.value = false
            }
        }
    }
} 