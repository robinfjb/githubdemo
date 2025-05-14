package com.example.githubdemo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import android.util.Base64
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.data.local.GitHubDatabase
import com.example.githubdemo.data.local.entity.toEntity
import com.example.githubdemo.data.local.entity.toRepository
import com.example.githubdemo.data.local.entity.toTopic
import com.example.githubdemo.data.model.Issue
import com.example.githubdemo.data.model.IssueRequest
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.model.Topic
import com.example.githubdemo.data.model.User
import com.example.githubdemo.data.remote.GitHubService
import com.example.githubdemo.di.AppContainer
import com.example.githubdemo.util.Constants
import com.example.githubdemo.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * GitHub数据仓库实现
 */
class GitHubRepositoryImpl(
    private val gitHubService: GitHubService,
    private val database: GitHubDatabase
) : GitHubRepository {

    private val repositoryDao = database.repositoryDao()
    private val topicDao = database.topicDao()

    override fun getTrendingRepositories(): Flow<List<Repository>> {
        return repositoryDao.getTrendingRepositories()
            .map { entities ->
                entities.map { it.toRepository() }
            }
    }

    override fun getFeaturedTopics(): Flow<List<Topic>> {
        return topicDao.getFeaturedTopics()
            .map { entities -> entities.map { it.toTopic() } }
    }

    override suspend fun searchRepositories(
        query: String,
        sort: String,
        page: Int,
        perPage: Int
    ): Flow<List<Repository>> {
        return flow {
            val response = gitHubService.searchRepositories(
                query = query, 
                sort = sort,
                page = page,
                perPage = perPage
            )
            val repositories = response.items

            // 只有第一页数据需要缓存到数据库
            if (page == 1) {
                withContext(Dispatchers.IO) {
                    repositoryDao.insertRepositories(repositories.map { it.toEntity() })
                }
            }

            emit(repositories)
        }.catch { e ->
            // 如果API调用失败，尝试从本地数据库获取结果
            emitAll(repositoryDao.searchRepositories(query)
                .map { entities -> entities.map { it.toRepository() } })
        }
    }

    override suspend fun getUserProfile(): User = withContext(Dispatchers.IO) {
        gitHubService.getUserProfile()
    }

    override suspend fun getUserRepositories(): List<Repository> = withContext(Dispatchers.IO) {
        gitHubService.getUserRepositories()
    }

    override suspend fun getRepositoryDetails(owner: String, repo: String): Repository =
        withContext(Dispatchers.IO) {
            gitHubService.getRepositoryDetails(owner, repo)
        }

    override suspend fun getRepositoryIssues(owner: String, repo: String): List<Issue> =
        withContext(Dispatchers.IO) {
            gitHubService.getRepositoryIssues(owner, repo)
        }

    override suspend fun createIssue(owner: String, repo: String, issue: IssueRequest): Issue =
        withContext(Dispatchers.IO) {
            gitHubService.createIssue(owner, repo, issue)
        }
        
    override suspend fun getIssueDetail(owner: String, repo: String, issueNumber: Int): Issue =
        withContext(Dispatchers.IO) {
            gitHubService.getIssueDetail(owner, repo, issueNumber)
        }

    override suspend fun getRepositoryReadme(owner: String, repo: String): String =
        withContext(Dispatchers.IO) {
            try {
                val response = gitHubService.getRepositoryReadme(owner, repo)
                // GitHub API返回的README内容是Base64编码的
                val decodedBytes = Base64.decode(response.content, Base64.DEFAULT)
                String(decodedBytes, Charset.defaultCharset())
            } catch (e: Exception) {
                // 如果获取README失败，返回错误信息
                "无法加载README文件: ${e.message}"
            }
        }


    override suspend fun refreshHomeData() = withContext(Dispatchers.IO) {
        try {
            // 刷新热门仓库
            val trendingRepos = gitHubService.getTrendingRepositories()
            repositoryDao.clearTrendingRepositories()
            repositoryDao.insertRepositories(trendingRepos.map { it.toEntity(isTrending = true) })

            // 刷新热门话题
            val topicsResponse = gitHubService.searchTopics()
            topicDao.clearTopics()
            topicDao.insertTopics(topicsResponse.items.map { it.toEntity() })
        } catch (e: Exception) {
            // 如果刷新失败，保留现有数据
            e.printStackTrace()
        }
    }

    override suspend fun clearExpiredData() = withContext(Dispatchers.IO) {
        val cutoffTime = Date().time - TimeUnit.MINUTES.toMillis(Constants.CACHE_TIMEOUT_MINUTES)
        repositoryDao.deleteExpiredData(cutoffTime)
        topicDao.deleteExpiredData(cutoffTime)
    }
} 