package com.example.githubdemo.data.remote

import com.example.githubdemo.data.model.AccessToken
import com.example.githubdemo.util.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class AuthServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var authService: AuthService
    
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        authService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `getAccessToken should parse response correctly`() = runBlocking {
        // 准备模拟响应数据
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""
                {
                    "access_token": "test_access_token",
                    "token_type": "bearer",
                    "scope": "repo,user"
                }
            """.trimIndent())
        
        // 设置模拟服务器返回模拟响应
        mockWebServer.enqueue(mockResponse)
        
        // 调用被测试的方法
        val accessToken = authService.getAccessToken(
            clientId = "test_client_id",
            clientSecret = "test_client_secret",
            code = "test_code",
            redirectUri = "test_redirect_uri"
        )
        
        // 验证结果
        assertEquals("test_access_token", accessToken.accessToken)
        assertEquals("bearer", accessToken.tokenType)
        assertEquals("repo,user", accessToken.scope)
        
        // 验证请求是否正确
        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/login/oauth/access_token", request.path)
    }
    
    @Test
    fun `getAccessToken should handle error response`() = runBlocking {
        // 准备模拟错误响应
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setBody("""
                {
                    "error": "bad_verification_code",
                    "error_description": "The code passed is incorrect or expired."
                }
            """.trimIndent())
        
        // 设置模拟服务器返回错误响应
        mockWebServer.enqueue(mockResponse)
        
        try {
            // 调用被测试的方法
            authService.getAccessToken(
                clientId = "test_client_id",
                clientSecret = "test_client_secret",
                code = "invalid_code",
                redirectUri = "test_redirect_uri"
            )
            
            // 如果没有抛出异常，测试失败
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            // 期望抛出异常
            assert(true)
        }
    }
} 