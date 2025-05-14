package com.example.githubdemo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubdemo.data.model.AccessToken
import com.example.githubdemo.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 认证ViewModel
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState
    
    /**
     * 获取GitHub认证URL
     */
    fun getAuthorizationUrl(): String {
        return authRepository.getAuthorizationUrl()
    }
    
    /**
     * 使用授权码获取访问令牌
     */
    suspend fun getAccessToken(code: String): AccessToken {
        _authState.value = AuthState.Loading
        
        try {
            val token = authRepository.getAccessToken(code)
            authRepository.saveAccessToken(token.accessToken)
            _authState.value = AuthState.Authenticated
            return token
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "未知错误")
            throw e
        }
    }
    
    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        val isLoggedIn = authRepository.isLoggedIn()
        _authState.value = if (isLoggedIn) AuthState.Authenticated else AuthState.Unauthenticated
        return isLoggedIn
    }
    
    /**
     * 退出登录
     */
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
    }
}

/**
 * 认证状态密封类
 */
sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
} 