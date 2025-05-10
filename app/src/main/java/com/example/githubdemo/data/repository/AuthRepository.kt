package com.example.githubdemo.data.repository

import com.example.githubdemo.data.model.AccessToken

/**
 * 认证数据仓库接口
 */
interface AuthRepository {
    
    /**
     * 获取认证URL
     */
    fun getAuthorizationUrl(): String
    
    /**
     * 使用授权码获取访问令牌
     */
    suspend fun getAccessToken(code: String): AccessToken
    
    /**
     * 保存访问令牌
     */
    fun saveAccessToken(token: String)
    
    /**
     * 获取访问令牌
     */
    fun getAccessToken(): String?
    
    /**
     * 保存用户ID
     */
    fun saveUserId(userId: Long)
    
    /**
     * 获取用户ID
     */
    fun getUserId(): Long
    
    /**
     * 保存用户登录名
     */
    fun saveUserLogin(login: String)
    
    /**
     * 获取用户登录名
     */
    fun getUserLogin(): String?
    
    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean
    
    /**
     * 清除所有存储的令牌和用户信息（登出）
     */
    fun logout()
} 