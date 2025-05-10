package com.example.githubdemo.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentProfileBinding
import com.example.githubdemo.ui.auth.WebViewAuthActivity
import com.example.githubdemo.ui.profile.adapter.RepositoryAdapter
import com.example.githubdemo.ui.repository.RepositoryDetailFragment
import com.example.githubdemo.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * 个人资料Fragment
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var repositoryAdapter: RepositoryAdapter
    
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            (requireActivity().application as GitHubApp).appContainer.authRepository,
            (requireActivity().application as GitHubApp).appContainer.gitHubRepository
        )
    }
    
    // 处理WebView授权结果
    private val authResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // 获取授权码
            val authCode = result.data?.getStringExtra("auth_code")
            authCode?.let {
                // 使用ViewModel处理授权码
                viewModel.handleAuthorizationCode(it)
            } ?: run {
                Snackbar.make(binding.root, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
            Snackbar.make(binding.root, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepositoriesRecyclerView()
        setupLoginButton()
        setupLogoutButton()
        observeViewModel()
    }

    private fun setupRepositoriesRecyclerView() {
        repositoryAdapter = RepositoryAdapter { repository ->
            // 使用FragmentTransaction替代Navigation
            val repositoryDetailFragment = RepositoryDetailFragment.newInstance(
                repository.owner.login,
                repository.name
            )
            
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, repositoryDetailFragment)
                .addToBackStack(null)
                .commit()
        }
        
        binding.repositoriesRecyclerView.adapter = repositoryAdapter
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val authUrl = viewModel.getAuthorizationUrl()
            
            // 创建WebViewAuthActivity的Intent
            val intent = WebViewAuthActivity.createIntent(
                requireContext(),
                authUrl,
                Constants.REDIRECT_URI
            )
            
            // 启动授权活动并等待结果
            authResultLauncher.launch(intent)
        }
    }
    
    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }
    
    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirmation_title)
            .setMessage(R.string.logout_confirmation_message)
            .setPositiveButton(R.string.logout) { _, _ ->
                performLogout()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun performLogout() {
        viewModel.logout()
        Snackbar.make(binding.root, getString(R.string.logout_success), Snackbar.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        // 观察认证状态
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            updateUiForAuthState(isLoggedIn)
        }
        
        // 观察授权过程状态
        viewModel.authStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                AuthStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Snackbar.make(binding.root, getString(R.string.auth_loading), Snackbar.LENGTH_SHORT).show()
                }
                AuthStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, getString(R.string.auth_success), Snackbar.LENGTH_SHORT).show()
                }
                AuthStatus.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT).show()
                }
                else -> { /* 不处理 */ }
            }
        }
        
        // 观察用户数据
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                updateUserData(it)
            }
        }
        
        // 观察用户仓库
        viewModel.userRepositories.observe(viewLifecycleOwner) { repositories ->
            repositoryAdapter.submitList(repositories)
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // 观察错误状态
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUiForAuthState(isLoggedIn: Boolean) {
        binding.apply {
            notLoggedInLayout.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
            nestedScrollView.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
            appBarLayout.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        }
        
        if (isLoggedIn) {
            viewModel.loadUserData()
        }
    }

    private fun updateUserData(user: ProfileUIModel) {
        binding.apply {
            userNameTextView.text = user.name
            userLoginTextView.text = "@${user.login}"
            bioTextView.text = user.bio ?: ""
            
            repoCountTextView.text = user.publicRepos.toString()
            followersCountTextView.text = user.followers.toString()
            followingCountTextView.text = user.following.toString()
            
            Glide.with(requireContext())
                .load(user.avatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(avatarImageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 