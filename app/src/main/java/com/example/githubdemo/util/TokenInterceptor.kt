package com.example.githubdemo.util

import com.blankj.utilcode.util.LogUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Token拦截器，为每个请求添加认证头
 */
class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github+json")
        // 如果用户已登录，为每个请求添加认证头
        val accessToken = tokenManager.getAccessToken()
        LogUtils.d("获取到的AccessToken: $accessToken")
        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "token $accessToken")
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
} 