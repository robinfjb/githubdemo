package com.example.githubdemo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.githubdemo.data.local.dao.RepositoryDao
import com.example.githubdemo.data.local.dao.TopicDao
import com.example.githubdemo.data.local.entity.RepositoryEntity
import com.example.githubdemo.data.local.entity.TopicEntity

/**
 * 应用程序本地数据库
 */
@Database(
    entities = [RepositoryEntity::class, TopicEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GitHubDatabase : RoomDatabase() {
    
    /**
     * 获取仓库DAO
     */
    abstract fun repositoryDao(): RepositoryDao
    
    /**
     * 获取话题DAO
     */
    abstract fun topicDao(): TopicDao
    
    companion object {
        @Volatile
        private var INSTANCE: GitHubDatabase? = null
        
        /**
         * 获取数据库实例
         */
        fun getDatabase(context: Context): GitHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GitHubDatabase::class.java,
                    "github_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 