package com.example.githubdemo.data.remote

import com.example.githubdemo.data.model.*
import retrofit2.http.*

interface GitHubService {
    
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): SearchResponse
    
    @GET("search/topics")
    suspend fun searchTopics(
        @Query("q") query: String = "is:featured",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): TopicsResponse
    
    @GET("repositories")
    suspend fun getTrendingRepositories(
        @Query("sort") sort: String = "stars"
    ): List<Repository>
    
    @GET("user")
    suspend fun getUserProfile(): User
    
    @GET("user/repos")
    suspend fun getUserRepositories(
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 20
    ): List<Repository>
    
    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Repository

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: IssueRequest
    ): Issue
    
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "all",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<Issue>
    
    @GET("repos/{owner}/{repo}/issues/{issue_number}")
    suspend fun getIssueDetail(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("issue_number") issueNumber: Int
    ): Issue
    
    @GET("repos/{owner}/{repo}/readme")
    suspend fun getRepositoryReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReadmeResponse
} 