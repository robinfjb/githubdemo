package com.example.githubdemo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.githubdemo.databinding.ActivityMainBinding
import com.example.githubdemo.ui.home.HomeFragment
import com.example.githubdemo.ui.profile.ProfileFragment
import com.example.githubdemo.ui.search.SearchFragment

/**
 * 主Activity，包含底部导航栏和Fragment切换
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainFragments = listOf(
        HomeFragment::class.java.name,
        SearchFragment::class.java.name,
        ProfileFragment::class.java.name
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
        
        // 处理OAuth回调
        handleAuthIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthIntent(intent)
    }
    
    private fun handleAuthIntent(intent: Intent) {
        // 处理GitHub OAuth回调
        if (intent.data?.scheme == "githubdemo") {
            val profileFragment = ProfileFragment()
            loadFragment(profileFragment)
            binding.bottomNavigation.selectedItemId = R.id.nav_profile
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private var lastBackPressTime: Long = 0
    private val backPressThreshold = 2000 // 2秒内再次点击才有效
    
    override fun onBackPressed() {
        // 获取当前Fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        
        // 判断当前是否为主页面Fragment
        if (currentFragment != null && mainFragments.contains(currentFragment.javaClass.name)) {
            // 在主页面，需要双击确认退出
            confirmExitOrNavigateBack()
        } else {
            // 在其他页面，正常回退
            super.onBackPressed()
        }
    }
    
    private fun confirmExitOrNavigateBack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < backPressThreshold) {
            showExitConfirmationDialog()
        } else {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
            lastBackPressTime = currentTime
        }
    }
    
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_app)
            .setMessage(R.string.confirm_exit)
            .setPositiveButton(R.string.confirm) { _, _ ->
                // 用户确认退出
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
} 