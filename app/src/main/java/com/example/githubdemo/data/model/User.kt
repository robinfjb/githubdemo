package com.example.githubdemo.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    val login: String,
    val name: String?,
    val bio: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("public_repos")
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    @SerializedName("created_at")
    val createdAt: String
) 