package com.example.githubdemo.ui.profile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class ProfileFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToProfile() {
        // 点击底部导航栏的个人按钮，导航到个人页面
        onView(withId(R.id.nav_profile)).perform(click())
    }

    @Test
    fun testProfileFragmentDisplayed() {
        // 验证个人资料页面是否显示
        onView(withId(R.id.profile_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonDisplayed() {
        // 在未登录状态下，验证登录按钮是否显示
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginButtonClick() {
        // 点击登录按钮，验证登录流程
        onView(withId(R.id.login_button)).perform(click())
        // 通常这里会启动登录WebView，但是在测试中可能需要模拟
    }

    @Test
    fun testUserInfoDisplayedWhenLoggedIn() {
        // 此测试需要已登录状态，可能需要使用模拟对象来设置状态
        // 验证用户头像、名称和统计信息是否显示（需要根据实际UI调整）
        // 这里提供一个简单的示例
        // 假设在已登录状态下，会显示个人资料区域
        onView(withId(R.id.user_info_container)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testRepositoriesListDisplayed() {
        // 验证用户仓库列表是否显示
        onView(withId(R.id.user_repositories_recycler_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogoutButtonDisplayed() {
        // 在登录状态下，验证登出按钮是否显示
        onView(withId(R.id.logout_button)).check(matches(isDisplayed()))
    }
} 