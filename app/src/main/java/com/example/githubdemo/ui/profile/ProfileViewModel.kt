package com.example.githubdemo.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.model.User
import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.GitHubRepository
import kotlinx.coroutines.launch

/**
 * 个人资料ViewModel
 */
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _userData = MutableLiveData<ProfileUIModel?>()
    val userData: LiveData<ProfileUIModel?> = _userData

    private val _userRepositories = MutableLiveData<List<Repository>>()
    val userRepositories: LiveData<List<Repository>> = _userRepositories

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error
    
    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> = _authStatus
    
    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        _isLoggedIn.value = authRepository.isLoggedIn()
    }

    fun getAuthorizationUrl(): String {
        return authRepository.getAuthorizationUrl()
    }
    
    /**
     * 处理GitHub授权码
     */
    fun handleAuthorizationCode(code: String) {
        _isLoading.value = true
        _authStatus.value = AuthStatus.LOADING
        
        viewModelScope.launch {
            try {
                // 使用授权码获取访问令牌
                val accessToken = authRepository.getAccessToken(code)
                
                // 保存访问令牌
                authRepository.saveAccessToken(accessToken.accessToken)
                
                // 获取用户信息
                val user = gitHubRepository.getUserProfile()
                
                // 保存用户ID和登录名
                authRepository.saveUserId(user.id)
                authRepository.saveUserLogin(user.login)
                
                // 更新登录状态
                _isLoggedIn.value = true
                _authStatus.value = AuthStatus.SUCCESS
                
                // 加载用户数据
                loadUserData()
            } catch (e: Exception) {
                _error.value = e.message ?: "授权失败"
                _authStatus.value = AuthStatus.ERROR
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserData() {
        if (!authRepository.isLoggedIn()) {
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            
            try {
                // 获取用户信息
                val user = gitHubRepository.getUserProfile()
                _userData.value = user.toUIModel()
                
                // 获取用户仓库
                val repositories = gitHubRepository.getUserRepositories()
                _userRepositories.value = repositories
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message ?: "无法加载用户数据"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        authRepository.logout()
        _isLoggedIn.value = false
        _userData.value = null
        _userRepositories.value = emptyList()
    }
}

/**
 * 授权状态
 */
enum class AuthStatus {
    LOADING,
    SUCCESS,
    ERROR
}

/**
 * 用户界面模型
 */
data class ProfileUIModel(
    val id: Long,
    val login: String,
    val name: String?,
    val bio: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int,
    val publicRepos: Int
)

/**
 * 将用户数据模型转换为UI模型
 */
fun User.toUIModel(): ProfileUIModel {
    return ProfileUIModel(
        id = id,
        login = login,
        name = name,
        bio = bio,
        avatarUrl = avatarUrl,
        followers = followers,
        following = following,
        publicRepos = publicRepos
    )
} 