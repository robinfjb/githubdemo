package com.example.githubdemo.ui.search

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.githubdemo.MainActivity
import com.example.githubdemo.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToSearch() {
        // 点击底部导航栏的搜索按钮，导航到搜索页面
        onView(withId(R.id.nav_search)).perform(click())
    }

    @Test
    fun testSearchFragmentDisplayed() {
        // 验证搜索输入框是否显示
        onView(withId(R.id.search_input)).check(matches(isDisplayed()))
    }

    @Test
    fun testSearchFunctionality() {
        // 在搜索框中输入文本
        onView(withId(R.id.search_input)).perform(typeText("android"), pressImeActionButton())
        
        // 验证搜索结果列表是否显示
        onView(withId(R.id.search_results_recycler_view)).check(matches(isDisplayed()))
        
        // 验证进度条是否显示后隐藏
        onView(withId(R.id.search_progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testLanguageChipsDisplayed() {
        // 验证语言筛选器是否显示
        onView(withId(R.id.language_chips_group)).check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyStateDisplayed() {
        // 清空搜索框内容
        onView(withId(R.id.search_input)).perform(clearText())
        
        // 验证空状态视图是否显示
        onView(withId(R.id.empty_state_view)).check(matches(isDisplayed()))
    }
} 