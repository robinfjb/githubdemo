package com.example.githubdemo.util

import android.content.Context
import android.content.SharedPreferences
import com.example.githubdemo.util.Constants.KEY_ACCESS_TOKEN
import com.example.githubdemo.util.Constants.KEY_USER_ID
import com.example.githubdemo.util.Constants.KEY_USER_LOGIN
import com.example.githubdemo.util.Constants.PREF_NAME

/**
 * Token管理器，负责保存和获取OAuth令牌
 */
class TokenManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * 保存访问令牌
     */
    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    
    /**
     * 获取访问令牌
     */
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * 保存用户ID
     */
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }
    
    /**
     * 获取用户ID
     */
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1)
    }
    
    /**
     * 保存用户登录名
     */
    fun saveUserLogin(login: String) {
        prefs.edit().putString(KEY_USER_LOGIN, login).apply()
    }
    
    /**
     * 获取用户登录名
     */
    fun getUserLogin(): String? {
        return prefs.getString(KEY_USER_LOGIN, null)
    }
    
    /**
     * 清除所有存储的令牌和用户信息
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
    
    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
} 