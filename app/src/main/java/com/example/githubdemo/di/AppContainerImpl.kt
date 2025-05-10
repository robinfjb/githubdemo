package com.example.githubdemo.di

import android.content.Context
import com.example.githubdemo.data.local.GitHubDatabase
import com.example.githubdemo.data.remote.AuthService
import com.example.githubdemo.data.remote.GitHubService
import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.AuthRepositoryImpl
import com.example.githubdemo.data.repository.GitHubRepository
import com.example.githubdemo.data.repository.GitHubRepositoryImpl
import com.example.githubdemo.util.Constants
import com.example.githubdemo.util.TokenInterceptor
import com.example.githubdemo.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 应用程序依赖容器实现
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    
    // 创建TokenManager
    private val tokenManager by lazy {
        TokenManager(applicationContext)
    }
    
    // 创建TokenInterceptor
    private val tokenInterceptor by lazy {
        TokenInterceptor(tokenManager)
    }
    
    // 创建HttpLoggingInterceptor
    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    // 创建Auth API客户端
    private val authOkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    // 创建GitHub API客户端
    private val githubOkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    // 创建Auth API Retrofit实例
    private val authRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.AUTH_API_URL)
            .client(authOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // 创建GitHub API Retrofit实例
    private val githubRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.GITHUB_API_URL)
            .client(githubOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // 创建Auth服务
    private val authService: AuthService by lazy {
        authRetrofit.create(AuthService::class.java)
    }
    
    // 创建GitHub服务
    private val githubService: GitHubService by lazy {
        githubRetrofit.create(GitHubService::class.java)
    }
    
    // 创建数据库
    private val database: GitHubDatabase by lazy {
        GitHubDatabase.getDatabase(applicationContext)
    }
    
    // 创建仓库
    override val gitHubRepository: GitHubRepository by lazy {
        GitHubRepositoryImpl(githubService, database)
    }
    
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authService, tokenManager)
    }
} 