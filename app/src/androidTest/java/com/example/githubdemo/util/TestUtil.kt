package com.example.githubdemo.util

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import com.example.githubdemo.data.model.AccessToken
import com.example.githubdemo.data.model.Owner
import com.example.githubdemo.data.model.Repository
import com.example.githubdemo.data.model.User

/**
 * 测试工具类，提供模拟数据和辅助方法
 */
object TestUtil {
    
    /**
     * 创建模拟的AccessToken对象
     */
    fun createMockAccessToken(): AccessToken {
        return AccessToken(
            accessToken = "mock_access_token",
            scope = "repo,user",
            tokenType = "bearer"
        )
    }
    
    /**
     * 创建模拟的User对象
     */
    fun createMockUser(): User {
        return User(
            id = 12345L,
            login = "test_user",
            name = "Test User",
            avatarUrl = "https://github.com/avatar.png",
            bio = "This is a test user",
            company = "Test Company",
            location = "Test Location",
            email = "test@example.com",
            publicRepos = 10,
            followers = 100,
            following = 50,
            createdAt = "2020-01-01T00:00:00Z"
        )
    }
    
    /**
     * 创建模拟的Repository列表
     */
    fun createMockRepositories(count: Int = 5): List<Repository> {
        val repositories = mutableListOf<Repository>()
        
        for (i in 1..count) {
            repositories.add(
                Repository(
                    id = i.toLong(),
                    name = "repository_$i",
                    fullName = "test_user/repository_$i",
                    description = "This is repository $i",
                    owner = Owner(
                        id = 12345L,
                        login = "test_user",
                        avatarUrl = "https://github.com/avatar.png"
                    ),
                    stargazersCount = i * 100,
                    forksCount = i * 10,
                    watchersCount = i * 5,
                    language = "Kotlin",
                    defaultBranch = "main",
                    createdAt = "2020-01-01T00:00:00Z",
                    updatedAt = "2020-01-01T00:00:00Z",
                    homepage = "https://github.com/test_user/repository_$i",
                    htmlUrl = "https://github.com/test_user/repository_$i",
                    openIssues = i,
                    license = null
                )
            )
        }
        
        return repositories
    }
    
    /**
     * 注册自定义的IdlingResource
     */
    fun registerIdlingResource(idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().register(idlingResource)
    }
    
    /**
     * 注销自定义的IdlingResource
     */
    fun unregisterIdlingResource(idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
} 