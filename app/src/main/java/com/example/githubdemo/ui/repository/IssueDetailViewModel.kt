package com.example.githubdemo.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.data.repository.GitHubRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 问题详情ViewModel
 */
class IssueDetailViewModel(
    private val ownerLogin: String,
    private val repoName: String,
    private val issueNumber: Int,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    // 问题详情
    private val _issueDetail = MutableLiveData<IssueUIModel?>()
    val issueDetail: LiveData<IssueUIModel?> = _issueDetail
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误消息
    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error
    
    init {
        loadIssueDetail()
    }

    private fun loadIssueDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            
            try {
                val issue = gitHubRepository.getIssueDetail(ownerLogin, repoName, issueNumber)
                _issueDetail.value = issue.toUIModel()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "无法加载问题详情"
                _isLoading.value = false
            }
        }
    }
}

/**
 * 问题UI模型
 */
data class IssueUIModel(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val authorName: String,
    val formattedDate: String,
    val htmlUrl: String
)

/**
 * 将问题模型转换为UI模型
 */
fun Issue.toUIModel(): IssueUIModel {
    // 格式化创建时间
    val formattedDate = try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .parse(createdAt)
        date?.let { 
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it) 
        } ?: createdAt
    } catch (e: Exception) {
        createdAt
    }
    
    return IssueUIModel(
        id = id,
        number = number,
        title = title,
        body = body,
        state = state,
        authorName = user.login,
        formattedDate = formattedDate,
        htmlUrl = htmlUrl
    )
}

/**
 * 问题详情ViewModelFactory
 */
class IssueDetailViewModelFactory(
    private val ownerLogin: String,
    private val repoName: String,
    private val issueNumber: Int,
    private val gitHubRepository: GitHubRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IssueDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IssueDetailViewModel(ownerLogin, repoName, issueNumber, gitHubRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 