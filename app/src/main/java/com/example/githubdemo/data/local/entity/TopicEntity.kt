package com.example.githubdemo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.githubdemo.data.model.Topic
import java.util.Date

/**
 * 话题数据库实体
 */
@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey
    val name: String,
    val displayName: String?,
    val description: String?,
    val shortDescription: String?,
    val createdBy: String?,
    val released: String?,
    val featured: Boolean,
    val lastRefreshed: Long = Date().time
)

/**
 * 将话题实体转换为话题模型
 */
fun TopicEntity.toTopic(): Topic {
    return Topic(
        name = name,
        displayName = displayName,
        description = description,
        shortDescription = shortDescription,
        createdBy = createdBy,
        released = released,
        featured = featured
    )
}

/**
 * 将话题模型转换为话题实体
 */
fun Topic.toEntity(): TopicEntity {
    return TopicEntity(
        name = name,
        displayName = displayName,
        description = description,
        shortDescription = shortDescription,
        createdBy = createdBy,
        released = released,
        featured = featured
    )
} 