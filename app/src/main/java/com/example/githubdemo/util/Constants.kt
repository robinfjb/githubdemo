package com.example.githubdemo.util

object Constants {
    
    // GitHub API相关配置
    const val GITHUB_API_URL = "https://api.github.com/"
    const val AUTH_API_URL = "https://github.com/"

    // OAuth配置
    const val CLIENT_ID = ""
    const val CLIENT_SECRET = ""
    const val REDIRECT_URI = "githubdemo://callback"
    const val SCOPE = "repo user"
    
    // 本地存储键
    const val PREF_NAME = "github_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_LOGIN = "user_login"
    
    // 分页配置
    const val PAGE_SIZE = 30
    
    // 缓存配置
    const val CACHE_TIMEOUT_MINUTES = 30L
} 