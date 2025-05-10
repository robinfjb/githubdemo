package com.example.githubdemo.data.repository

import com.example.githubdemo.data.model.AccessToken
import com.example.githubdemo.data.remote.AuthService
import com.example.githubdemo.util.Constants
import com.example.githubdemo.util.TokenManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class AuthRepositoryTest {
    
    private lateinit var authService: AuthService
    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository
    
    @Before
    fun setUp() {
        authService = mock(AuthService::class.java)
        tokenManager = mock(TokenManager::class.java)
        authRepository = AuthRepositoryImpl(authService, tokenManager)
    }
    
    @Test
    fun `getAuthorizationUrl should return correct URL`() {
        // 调用被测试的方法
        val url = authRepository.getAuthorizationUrl()
        
        // 验证URL包含所需的参数
        assertTrue(url.startsWith("${Constants.AUTH_API_URL}login/oauth/authorize"))
        assertTrue(url.contains("client_id=${Constants.CLIENT_ID}"))
        assertTrue(url.contains("redirect_uri="))
        assertTrue(url.contains("scope="))
        assertTrue(url.contains("state="))
    }
    
    @Test
    fun `getAccessToken should forward call to service`() = runBlocking {
        // 准备模拟数据
        val mockCode = "test_code"
        val mockToken = AccessToken(
            accessToken = "test_access_token",
            tokenType = "bearer",
            scope = "repo,user"
        )
        
        // 设置mock行为
        `when`(authService.getAccessToken(
            clientId = Constants.CLIENT_ID,
            clientSecret = Constants.CLIENT_SECRET,
            code = mockCode,
            redirectUri = Constants.REDIRECT_URI
        )).thenReturn(mockToken)
        
        // 调用被测试的方法
        val result = authRepository.getAccessToken(mockCode)
        
        // 验证结果
        assertEquals(mockToken, result)
        
        // 验证调用了正确的方法
        verify(authService).getAccessToken(
            clientId = Constants.CLIENT_ID,
            clientSecret = Constants.CLIENT_SECRET,
            code = mockCode,
            redirectUri = Constants.REDIRECT_URI
        )
    }
    
    @Test
    fun `saveAccessToken should delegate to TokenManager`() {
        // 准备模拟数据
        val mockToken = "test_access_token"
        
        // 调用被测试的方法
        authRepository.saveAccessToken(mockToken)
        
        // 验证调用了正确的方法
        verify(tokenManager).saveAccessToken(mockToken)
    }
    
    @Test
    fun `getAccessToken from repository should delegate to TokenManager`() {
        // 准备模拟数据
        val mockToken = "test_access_token"
        `when`(tokenManager.getAccessToken()).thenReturn(mockToken)
        
        // 调用被测试的方法
        val result = authRepository.getAccessToken()
        
        // 验证结果和调用
        assertEquals(mockToken, result)
        verify(tokenManager).getAccessToken()
    }
    
    @Test
    fun `isLoggedIn should return true when token exists`() {
        // 准备模拟数据
        `when`(tokenManager.getAccessToken()).thenReturn("test_access_token")
        
        // 调用被测试的方法
        val result = authRepository.isLoggedIn()
        
        // 验证结果
        assertTrue(result)
        verify(tokenManager).getAccessToken()
    }
    
    @Test
    fun `isLoggedIn should return false when token does not exist`() {
        // 准备模拟数据
        `when`(tokenManager.getAccessToken()).thenReturn(null)
        
        // 调用被测试的方法
        val result = authRepository.isLoggedIn()
        
        // 验证结果
        assertFalse(result)
        verify(tokenManager).getAccessToken()
    }
    
    @Test
    fun `logout should delegate to TokenManager`() {
        // 调用被测试的方法
        authRepository.logout()
        
        // 验证调用了正确的方法
        verify(tokenManager).clearAll()
    }
} 