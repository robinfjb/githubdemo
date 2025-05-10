package com.example.githubdemo.data.repository

import com.example.githubdemo.data.model.AccessToken
import com.example.githubdemo.data.remote.AuthService
import com.example.githubdemo.util.Constants
import com.example.githubdemo.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

/**
 * 认证数据仓库实现
 */
class AuthRepositoryImpl(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    override fun getAuthorizationUrl(): String {
        val encodedRedirectUri = URLEncoder.encode(Constants.REDIRECT_URI, "UTF-8")
        val encodedScope = URLEncoder.encode(Constants.SCOPE, "UTF-8")
        
        return "${Constants.AUTH_API_URL}login/oauth/authorize" +
                "?client_id=${Constants.CLIENT_ID}" +
                "&redirect_uri=$encodedRedirectUri" +
                "&scope=$encodedScope" +
                "&state=${System.currentTimeMillis()}"
    }
    
    override suspend fun getAccessToken(code: String): AccessToken = withContext(Dispatchers.IO) {
        authService.getAccessToken(
            clientId = Constants.CLIENT_ID,
            clientSecret = Constants.CLIENT_SECRET,
            code = code,
            redirectUri = Constants.REDIRECT_URI
        )
    }
    
    override fun saveAccessToken(token: String) {
        tokenManager.saveAccessToken(token)
    }
    
    override fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }
    
    override fun saveUserId(userId: Long) {
        tokenManager.saveUserId(userId)
    }
    
    override fun getUserId(): Long {
        return tokenManager.getUserId()
    }
    
    override fun saveUserLogin(login: String) {
        tokenManager.saveUserLogin(login)
    }
    
    override fun getUserLogin(): String? {
        return tokenManager.getUserLogin()
    }
    
    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
    
    override fun logout() {
        tokenManager.clearAll()
    }
} 