package com.example.githubdemo.ui.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentIssueDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 问题详情Fragment
 */
class IssueDetailFragment : Fragment() {

    private var _binding: FragmentIssueDetailBinding? = null
    private val binding get() = _binding!!
    
    private var ownerLogin: String = ""
    private var repoName: String = ""
    private var issueNumber: Int = 0
    
    private val viewModel: IssueDetailViewModel by viewModels {
        IssueDetailViewModelFactory(
            ownerLogin,
            repoName,
            issueNumber,
            (requireActivity().application as GitHubApp).appContainer.gitHubRepository
        )
    }

    companion object {
        private const val ARG_OWNER_LOGIN = "owner_login"
        private const val ARG_REPO_NAME = "repo_name"
        private const val ARG_ISSUE_NUMBER = "issue_number"
        
        fun newInstance(ownerLogin: String, repoName: String, issueNumber: Int): IssueDetailFragment {
            val fragment = IssueDetailFragment()
            val args = Bundle()
            args.putString(ARG_OWNER_LOGIN, ownerLogin)
            args.putString(ARG_REPO_NAME, repoName)
            args.putInt(ARG_ISSUE_NUMBER, issueNumber)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ownerLogin = it.getString(ARG_OWNER_LOGIN, "")
            repoName = it.getString(ARG_REPO_NAME, "")
            issueNumber = it.getInt(ARG_ISSUE_NUMBER, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        // 观察问题详情
        viewModel.issueDetail.observe(viewLifecycleOwner) { issue ->
            issue?.let {
                updateIssueUI(it)
            }
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
        
        // 观察错误消息
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                binding.errorLayout.visibility = View.VISIBLE
                binding.contentLayout.visibility = View.GONE
            } else {
                binding.errorLayout.visibility = View.GONE
            }
        }
    }

    private fun updateIssueUI(issue: IssueUIModel) {
        binding.apply {
            issueTitleTextView.text = issue.title
            issueBodyTextView.text = issue.body ?: getString(R.string.no_description)
            
            issueNumberTextView.text = "#${issue.number}"
            
            // 格式化日期
            issueDateTextView.text = issue.formattedDate
            
            // 设置作者
            authorTextView.text = issue.authorName
            
            // 设置状态
            statusChip.text = issue.state
            statusChip.setChipBackgroundColorResource(
                if (issue.state == "open") R.color.github_red else R.color.github_green
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 