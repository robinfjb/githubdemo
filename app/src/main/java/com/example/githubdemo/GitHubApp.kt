package com.example.githubdemo

import android.app.Application
import com.example.githubdemo.di.AppContainer
import com.example.githubdemo.di.AppContainerImpl

class GitHubApp : Application() {
    
    // 应用程序级别的依赖容器
    lateinit var appContainer: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化依赖容器
        appContainer = AppContainerImpl(this)
    }
} 