package com.example.githubdemo.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubdemo.data.local.entity.RepositoryEntity

/**
 * 仓库数据访问对象
 */
@Dao
interface RepositoryDao {
    
    /**
     * 插入仓库列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)
    
    /**
     * 获取所有热门仓库
     */
    @Query("SELECT * FROM repositories WHERE isTrending = 1 ORDER BY stars DESC")
    fun getTrendingRepositories(): Flow<List<RepositoryEntity>>
    
    /**
     * 根据ID获取仓库
     */
    @Query("SELECT * FROM repositories WHERE id = :id")
    suspend fun getRepositoryById(id: Long): RepositoryEntity?
    
    /**
     * 搜索仓库（按名称和描述）
     */
    @Query("SELECT * FROM repositories WHERE " +
            "(name LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%') " +
            "ORDER BY stars DESC LIMIT :limit")
    fun searchRepositories(query: String, limit: Int = 50): Flow<List<RepositoryEntity>>
    
    /**
     * 按语言搜索仓库
     */
    @Query("SELECT * FROM repositories WHERE " +
            "language = :language " +
            "ORDER BY stars DESC LIMIT :limit")
    fun searchRepositoriesByLanguage(language: String, limit: Int = 50): Flow<List<RepositoryEntity>>

    /**
     * 清除所有热门仓库
     */
    @Query("DELETE FROM repositories WHERE isTrending = 1")
    suspend fun clearTrendingRepositories()
    
    /**
     * 删除过期的数据
     */
    @Query("DELETE FROM repositories WHERE lastRefreshed < :timestamp AND isTrending = 0")
    suspend fun deleteExpiredData(timestamp: Long)
} 