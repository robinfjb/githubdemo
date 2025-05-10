package com.example.githubdemo.di

import com.example.githubdemo.data.repository.AuthRepository
import com.example.githubdemo.data.repository.GitHubRepository

/**
 * 应用程序依赖容器接口
 */
interface AppContainer {
    val gitHubRepository: GitHubRepository
    val authRepository: AuthRepository
} 