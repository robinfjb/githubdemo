package com.example.githubdemo.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class GitHubServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var gitHubService: GitHubService
    
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        gitHubService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `searchRepositories should parse response correctly`() = runBlocking {
        // 准备模拟响应数据
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {
                    "total_count": 2,
                    "incomplete_results": false,
                    "items": [
                        {
                            "id": 1,
                            "name": "test-repo-1",
                            "full_name": "test-user/test-repo-1",
                            "description": "Test repository 1",
                            "owner": {
                                "id": 1001,
                                "login": "test-user",
                                "avatar_url": "https://github.com/avatar.png"
                            },
                            "html_url": "https://github.com/test-user/test-repo-1",
                            "language": "Kotlin",
                            "stargazers_count": 100,
                            "forks_count": 50,
                            "watchers_count": 30,
                            "default_branch": "main",
                            "created_at": "2020-01-01T00:00:00Z",
                            "updated_at": "2020-01-02T00:00:00Z",
                            "open_issues": 5
                        },
                        {
                            "id": 2,
                            "name": "test-repo-2",
                            "full_name": "test-user/test-repo-2",
                            "description": "Test repository 2",
                            "owner": {
                                "id": 1001,
                                "login": "test-user",
                                "avatar_url": "https://github.com/avatar.png"
                            },
                            "html_url": "https://github.com/test-user/test-repo-2",
                            "language": "Java",
                            "stargazers_count": 200,
                            "forks_count": 100,
                            "watchers_count": 60,
                            "default_branch": "main",
                            "created_at": "2020-01-01T00:00:00Z",
                            "updated_at": "2020-01-02T00:00:00Z",
                            "open_issues": 10
                        }
                    ]
                }
            """.trimIndent())
        
        // 设置模拟服务器返回模拟响应
        mockWebServer.enqueue(mockResponse)
        
        // 调用被测试的方法
        val searchResponse = gitHubService.searchRepositories("test")
        
        // 验证结果
        assertEquals(2, searchResponse.totalCount)
        assertEquals(2, searchResponse.items.size)
        assertEquals("test-repo-1", searchResponse.items[0].name)
        assertEquals("test-repo-2", searchResponse.items[1].name)
        assertEquals("Kotlin", searchResponse.items[0].language)
        assertEquals("Java", searchResponse.items[1].language)
        
        // 验证请求是否正确
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("/search/repositories?q=test") == true)
    }
    
    @Test
    fun `getUserProfile should parse response correctly`() = runBlocking {
        // 准备模拟响应数据
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {
                    "id": 1001,
                    "login": "test-user",
                    "name": "Test User",
                    "avatar_url": "https://github.com/avatar.png",
                    "bio": "This is a test user",
                    "public_repos": 30,
                    "followers": 1000,
                    "following": 500,
                    "created_at": "2020-01-01T00:00:00Z"
                }
            """.trimIndent())
        
        // 设置模拟服务器返回模拟响应
        mockWebServer.enqueue(mockResponse)
        
        // 调用被测试的方法
        val user = gitHubService.getUserProfile()
        
        // 验证结果
        assertEquals(1001, user.id)
        assertEquals("test-user", user.login)
        assertEquals("Test User", user.name)
        assertEquals("https://github.com/avatar.png", user.avatarUrl)
        assertEquals("This is a test user", user.bio)
        assertEquals(30, user.publicRepos)
        assertEquals(1000, user.followers)
        assertEquals(500, user.following)
        
        // 验证请求是否正确
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/user", request.path)
    }
    
    @Test
    fun `getRepositoryDetails should parse response correctly`() = runBlocking {
        // 准备模拟响应数据
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {
                    "id": 1,
                    "name": "test-repo",
                    "full_name": "test-user/test-repo",
                    "description": "Test repository",
                    "owner": {
                        "id": 1001,
                        "login": "test-user",
                        "avatar_url": "https://github.com/avatar.png"
                    },
                    "html_url": "https://github.com/test-user/test-repo",
                    "language": "Kotlin",
                    "stargazers_count": 100,
                    "forks_count": 50,
                    "watchers_count": 30,
                    "default_branch": "main",
                    "created_at": "2020-01-01T00:00:00Z",
                    "updated_at": "2020-01-02T00:00:00Z",
                    "open_issues": 5
                }
            """.trimIndent())
        
        // 设置模拟服务器返回模拟响应
        mockWebServer.enqueue(mockResponse)
        
        // 调用被测试的方法
        val repository = gitHubService.getRepositoryDetails("test-user", "test-repo")
        
        // 验证结果
        assertEquals(1, repository.id)
        assertEquals("test-repo", repository.name)
        assertEquals("test-user/test-repo", repository.fullName)
        assertEquals("Test repository", repository.description)
        assertEquals("test-user", repository.owner.login)
        assertEquals("Kotlin", repository.language)
        assertEquals(100, repository.stargazersCount)
        
        // 验证请求是否正确
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/repos/test-user/test-repo", request.path)
    }
} 