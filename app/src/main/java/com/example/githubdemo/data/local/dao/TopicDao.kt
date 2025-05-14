package com.example.githubdemo.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubdemo.data.local.entity.TopicEntity

/**
 * 话题数据访问对象
 */
@Dao
interface TopicDao {
    
    /**
     * 插入话题列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)
    
    /**
     * 获取所有热门话题
     */
    @Query("SELECT * FROM topics WHERE featured = 1 ORDER BY name ASC")
    fun getFeaturedTopics(): Flow<List<TopicEntity>>
    
    /**
     * 根据名称获取话题
     */
    @Query("SELECT * FROM topics WHERE name = :name")
    suspend fun getTopicByName(name: String): TopicEntity?
    
    /**
     * 搜索话题
     */
    @Query("SELECT * FROM topics WHERE " +
            "name LIKE '%' || :query || '%' OR " +
            "displayName LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' " +
            "ORDER BY featured DESC, name ASC LIMIT :limit")
    fun searchTopics(query: String, limit: Int = 50): Flow<List<TopicEntity>>
    
    /**
     * 删除过期的数据
     */
    @Query("DELETE FROM topics WHERE lastRefreshed < :timestamp")
    suspend fun deleteExpiredData(timestamp: Long)

    /**
     * 清除所有热门仓库
     */
    @Query("DELETE FROM topics")
    suspend fun clearTopics()
} 