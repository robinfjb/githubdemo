package com.example.githubdemo.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.GitHubRepository
import kotlinx.coroutines.launch

/**
 * 仓库详情ViewModel
 */
class RepositoryDetailViewModel(
    private val ownerLogin: String,
    private val repoName: String,
    private val gitHubRepository: GitHubRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 仓库数据
    private val _repositoryData = MutableLiveData<RepositoryUIModel?>()
    val repositoryData: LiveData<RepositoryUIModel?> = _repositoryData
    
    // 问题列表
    private val _issues = MutableLiveData<List<Issue>>()
    val issues: LiveData<List<Issue>> = _issues
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误消息
    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error
    
    // 登录状态
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    // README内容
    private val _readmeContent = MutableLiveData<String>()
    val readmeContent: LiveData<String> = _readmeContent
    
    // README加载状态
    private val _isReadmeLoading = MutableLiveData<Boolean>(true)
    val isReadmeLoading: LiveData<Boolean> = _isReadmeLoading
    
    init {
        _isLoggedIn.value = authRepository.isLoggedIn()
        loadRepositoryData()
        loadReadmeContent()
    }

    private fun loadRepositoryData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            
            try {
                val repository = gitHubRepository.getRepositoryDetails(ownerLogin, repoName)
                _repositoryData.value = repository.toUIModel()

                loadIssues()
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "无法加载仓库数据"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 刷新问题列表
     */
    fun refreshIssues() {
        viewModelScope.launch {
            try {
                loadIssues()
            } catch (e: Exception) {
                _error.value = e.message ?: "无法刷新问题列表"
            }
        }
    }
    
    /**
     * 加载问题列表
     */
    private suspend fun loadIssues() {
        val issues = gitHubRepository.getRepositoryIssues(ownerLogin, repoName)
        _issues.value = issues
    }
    
    private fun loadReadmeContent() {
        viewModelScope.launch {
            _isReadmeLoading.value = true
            
            try {
                val readme = gitHubRepository.getRepositoryReadme(ownerLogin, repoName)
                _readmeContent.value = readme
                _isReadmeLoading.value = false
            } catch (e: Exception) {
                _readmeContent.value = "无法加载README文件"
                _isReadmeLoading.value = false
            }
        }
    }
}

/**
 * 仓库UI模型
 */
data class RepositoryUIModel(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val openIssues: Int,
    val language: String?,
    val htmlUrl: String
)

/**
 * 将仓库模型转换为UI模型
 */
fun Repository.toUIModel(): RepositoryUIModel {
    return RepositoryUIModel(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        stars = stars,
        forks = forks,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl
    )
} 