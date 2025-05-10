package com.example.githubdemo.util

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

/**
 * 用于Espresso测试中处理异步操作的IdlingResource
 * 
 * 在执行异步操作前，调用increment()方法增加计数
 * 在异步操作完成后，调用decrement()方法减少计数
 * 当计数为0时，Espresso将继续执行测试
 */
object EspressoIdlingResource {
    
    private const val RESOURCE_NAME = "GLOBAL"
    
    private val countingIdlingResource = CountingIdlingResource(RESOURCE_NAME)
    
    /**
     * 获取IdlingResource实例
     */
    val idlingResource: IdlingResource
        get() = countingIdlingResource
    
    /**
     * 增加计数
     */
    fun increment() {
        countingIdlingResource.increment()
    }
    
    /**
     * 确保安全地减少计数
     */
    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
} 