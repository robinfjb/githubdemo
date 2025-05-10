package com.example.githubdemo.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.githubdemo.MainActivity
import com.example.githubdemo.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testHomeFragmentDisplayed() {
        // 验证首页标题是否显示
        onView(withId(R.id.home_title)).check(matches(isDisplayed()))
        onView(withId(R.id.home_title)).check(matches(withText(R.string.trending_repositories)))
    }

    @Test
    fun testTrendingRepositoriesSectionDisplayed() {
        // 验证trending repositories部分是否显示
        onView(withId(R.id.trending_repositories_recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testTopicSectionDisplayed() {
        // 验证topics部分是否显示
        onView(withId(R.id.topics_recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testSwipeToRefresh() {
        // 验证下拉刷新组件是否存在
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()))
    }
} 