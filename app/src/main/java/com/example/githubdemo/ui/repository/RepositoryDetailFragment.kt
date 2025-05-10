package com.example.githubdemo.ui.repository

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.LogUtils
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentRepositoryDetailBinding
import com.example.githubdemo.event.IssueEvent
import com.example.githubdemo.ui.repository.adapter.IssueAdapter
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 仓库详情Fragment
 */
class RepositoryDetailFragment : Fragment() {

    private var _binding: FragmentRepositoryDetailBinding? = null
    private val binding get() = _binding!!
    
    private var ownerLogin: String = ""
    private var repoName: String = ""
    
    private lateinit var issueAdapter: IssueAdapter
    
    // README相关视图
    private lateinit var readmeContentTextView: TextView
    private lateinit var readmeProgressBar: ProgressBar
    
    private val viewModel: RepositoryDetailViewModel by viewModels {
        RepositoryDetailViewModelFactory(
            ownerLogin,
            repoName,
            (requireActivity().application as GitHubApp).appContainer.gitHubRepository,
            (requireActivity().application as GitHubApp).appContainer.authRepository
        )
    }

    companion object {
        private const val ARG_OWNER_LOGIN = "owner_login"
        private const val ARG_REPO_NAME = "repo_name"
        
        fun newInstance(ownerLogin: String, repoName: String): RepositoryDetailFragment {
            val fragment = RepositoryDetailFragment()
            val args = Bundle()
            args.putString(ARG_OWNER_LOGIN, ownerLogin)
            args.putString(ARG_REPO_NAME, repoName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ownerLogin = it.getString(ARG_OWNER_LOGIN, "")
            repoName = it.getString(ARG_REPO_NAME, "")
        }
        
        // 注册EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        // 取消注册EventBus
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupIssuesRecyclerView()
        setupReadmeViews()
        setupClickListeners()
        observeViewModel()
    }
    
    /**
     * 处理Issue创建成功事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIssueCreated(event: IssueEvent.IssueCreatedEvent) {
        if (event.ownerLogin == ownerLogin && event.repoName == repoName) {
            LogUtils.d( "刷新问题列表")
            viewModel.refreshIssues()
        }
    }

    private fun setupIssuesRecyclerView() {
        issueAdapter = IssueAdapter { issue ->
            navigateToIssueDetail(issue.number)
        }
        binding.issuesRecyclerView.adapter = issueAdapter
    }
    
    private fun navigateToIssueDetail(issueNumber: Int) {
        val issueDetailFragment = IssueDetailFragment.newInstance(ownerLogin, repoName, issueNumber)
        
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, issueDetailFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun setupReadmeViews() {
        val readmeLayout = binding.readmeLayout
        readmeContentTextView = readmeLayout.readmeContentTextView
        readmeProgressBar = readmeLayout.readmeProgressBar
    }

    private fun setupClickListeners() {
        binding.createIssueButton.setOnClickListener {
            val createIssueFragment = CreateIssueFragment.newInstance(ownerLogin, repoName)
            
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, createIssueFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel() {
        
        viewModel.repositoryData.observe(viewLifecycleOwner) { repository ->
            repository?.let {
                updateRepositoryUI(it)
            }
        }
        
        viewModel.issues.observe(viewLifecycleOwner) { issues ->
            issueAdapter.submitList(issues)
            binding.noIssuesTextView.visibility = if (issues.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                binding.errorLayout.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.GONE
            } else {
                binding.errorLayout.visibility = View.GONE
            }
        }
        
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            binding.createIssueButton.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        }
        
        viewModel.readmeContent.observe(viewLifecycleOwner) { readmeContent ->
            if (readmeContent.isNotEmpty()) {
                readmeContentTextView.text = readmeContent
                readmeContentTextView.visibility = View.VISIBLE
            } else {
                readmeContentTextView.visibility = View.GONE
            }
        }
        
        viewModel.isReadmeLoading.observe(viewLifecycleOwner) { isLoading ->
            readmeProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateRepositoryUI(repository: RepositoryUIModel) {
        binding.apply {
            repositoryNameTextView.text = repository.fullName
            repositoryDescriptionTextView.text = repository.description ?: getString(R.string.no_description)
            
            starsCountTextView.text = repository.stars.toString()
            forksCountTextView.text = repository.forks.toString()
            issuesCountTextView.text = repository.openIssues.toString()
            
            if (repository.language != null) {
                languageChip.text = repository.language
                languageChip.visibility = View.VISIBLE
            } else {
                languageChip.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 