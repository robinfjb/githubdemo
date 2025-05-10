package com.example.githubdemo.event

/**
 * Issue相关事件类
 */
class IssueEvent {
    /**
     * Issue创建成功事件
     * @param ownerLogin 仓库所有者登录名
     * @param repoName 仓库名称
     */
    data class IssueCreatedEvent(
        val ownerLogin: String,
        val repoName: String
    )
} 