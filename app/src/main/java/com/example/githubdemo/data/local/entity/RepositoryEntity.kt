package com.example.githubdemo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.githubdemo.data.model.Owner
import com.example.githubdemo.data.model.Repository
import java.util.Date

/**
 * 仓库数据库实体
 */
@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val ownerId: Long,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val ownerHtmlUrl: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val openIssues: Int,
    val language: String?,
    val htmlUrl: String,
    val createdAt: String?,
    val updatedAt: String?,
    val lastRefreshed: Long = Date().time,
    val isTrending: Boolean = false
)

/**
 * 将仓库实体转换为仓库模型
 */
fun RepositoryEntity.toRepository(): Repository {
    return Repository(
        id = id,
        name = name,
        fullName = fullName,
        owner = Owner(
            id = ownerId,
            login = ownerLogin,
            avatarUrl = ownerAvatarUrl,
            htmlUrl = ownerHtmlUrl
        ),
        description = description,
        stars = stars,
        forks = forks,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 将仓库模型转换为仓库实体
 */
fun Repository.toEntity(isTrending: Boolean = false): RepositoryEntity {
    return RepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        ownerId = owner.id,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        ownerHtmlUrl = owner.htmlUrl,
        description = description,
        stars = stars,
        forks = forks,
        openIssues = openIssues,
        language = language,
        htmlUrl = htmlUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isTrending = isTrending
    )
} 