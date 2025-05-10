package com.example.githubdemo.ui.repository

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.LogUtils
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentCreateIssueBinding
import com.example.githubdemo.event.IssueEvent
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus

/**
 * 创建问题Fragment
 */
class CreateIssueFragment : Fragment() {

    private var _binding: FragmentCreateIssueBinding? = null
    private val binding get() = _binding!!
    
    private var ownerLogin: String = ""
    private var repoName: String = ""
    
    private val viewModel: CreateIssueViewModel by viewModels {
        CreateIssueViewModelFactory(
            ownerLogin,
            repoName,
            (requireActivity().application as GitHubApp).appContainer.gitHubRepository
        )
    }
    
    companion object {
        private const val ARG_OWNER_LOGIN = "owner_login"
        private const val ARG_REPO_NAME = "repo_name"
        
        fun newInstance(ownerLogin: String, repoName: String): CreateIssueFragment {
            val fragment = CreateIssueFragment()
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
        LogUtils.d("onCreate: 创建问题页面创建 - $ownerLogin/$repoName")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateIssueBinding.inflate(inflater, container, false)
        LogUtils.d("onCreateView: 创建视图")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.d("onViewCreated: 视图已创建")
        
        setupSubmitButton()
        observeViewModel()
    }

    private fun setupSubmitButton() {
        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val body = binding.bodyEditText.text.toString().trim()

            LogUtils.d("提交按钮点击: 标题=$title, 内容长度=${body.length}")
            
            if (title.isEmpty()) {
                LogUtils.d(this, "标题为空")
                binding.titleInputLayout.error = getString(R.string.empty_field_error)
                return@setOnClickListener
            } else {
                binding.titleInputLayout.error = null
            }
            
            viewModel.createIssue(title, body)
            LogUtils.d("开始创建问题: $title")
        }
    }

    private fun observeViewModel() {
        LogUtils.d("开始观察ViewModel数据")
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            LogUtils.d("加载状态变化: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.submitButton.isEnabled = !isLoading
        }
        
        viewModel.issueCreated.observe(viewLifecycleOwner) { isCreated ->
            if (isCreated) {
                LogUtils.d("问题创建成功")
                Snackbar.make(binding.root, R.string.issue_created, Snackbar.LENGTH_SHORT).show()
                
                // 使用EventBus发送Issue创建成功事件
                EventBus.getDefault().post(IssueEvent.IssueCreatedEvent(ownerLogin, repoName))
                LogUtils.d("已发送问题创建事件")
                
                // 返回上一个Fragment
                requireActivity().supportFragmentManager.popBackStack()
                LogUtils.d("导航返回")
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                LogUtils.d("创建问题错误: $errorMessage")
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d("onDestroyView: 视图销毁")
        _binding = null
    }
} 