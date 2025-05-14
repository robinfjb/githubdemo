package com.example.githubdemo.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.data.model.IssueRequest
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.model.Topic
import com.example.githubdemo.data.model.User

/**
 * GitHub数据仓库接口
 */
interface GitHubRepository {
    
    /**
     * 获取热门仓库
     */
    fun getTrendingRepositories(): Flow<List<Repository>>
    
    /**
     * 获取热门话题
     */
    fun getFeaturedTopics(): Flow<List<Topic>>
    
    /**
     * 搜索仓库
     */
    suspend fun searchRepositories(
        query: String, 
        sort: String = "stars", 
        page: Int = 1, 
        perPage: Int = 30
    ): Flow<List<Repository>>
    
    /**
     * 获取用户个人资料
     */
    suspend fun getUserProfile(): User
    
    /**
     * 获取用户的仓库列表
     */
    suspend fun getUserRepositories(): List<Repository>
    
    /**
     * 获取仓库详情
     */
    suspend fun getRepositoryDetails(owner: String, repo: String): Repository
    
    /**
     * 获取仓库的问题列表
     */
    suspend fun getRepositoryIssues(owner: String, repo: String): List<Issue>
    
    /**
     * 创建问题
     */
    suspend fun createIssue(owner: String, repo: String, issue: IssueRequest): Issue
    
    /**
     * 获取问题详情
     */
    suspend fun getIssueDetail(owner: String, repo: String, issueNumber: Int): Issue
    
    /**
     * 刷新首页数据
     */
    suspend fun refreshHomeData()
    /**
     * 清理过期数据
     */
    suspend fun clearExpiredData()

    /**
     * 获取仓库README
     */
    suspend fun getRepositoryReadme(owner: String, repo: String): String
} 