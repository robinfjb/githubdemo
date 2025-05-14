package com.example.githubdemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentHomeBinding
import com.example.githubdemo.ui.home.adapter.RepositoryCardAdapter
import com.example.githubdemo.ui.home.adapter.TopicAdapter
import com.example.githubdemo.ui.repository.RepositoryDetailFragment
import com.example.githubdemo.ui.search.SearchFragment
import com.example.githubdemo.ui.home.adapter.ItemDecorationWithMargins
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 首页Fragment，显示热门仓库和话题
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var repositoryAdapter: RepositoryCardAdapter
    private lateinit var topicAdapter: TopicAdapter
    
    private val viewModel: HomeViewModel by viewModels { 
        HomeViewModelFactory((requireActivity().application as GitHubApp).appContainer.gitHubRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        repositoryAdapter = RepositoryCardAdapter { repository ->
            val repositoryDetailFragment = RepositoryDetailFragment.newInstance(
                repository.owner.login,
                repository.name
            )
            
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, repositoryDetailFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.trendingReposRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = repositoryAdapter
        }

        topicAdapter = TopicAdapter()
        binding.trendingTopicsRecyclerView.apply {
            addItemDecoration(ItemDecorationWithMargins(context, 16))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topicAdapter
        }
    }

    private fun setupClickListeners() {
        binding.retryButton.setOnClickListener {
            loadData()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { hasError ->
                binding.errorTextView.visibility = if (hasError) View.VISIBLE else View.GONE
                binding.retryButton.visibility = if (hasError) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trendingRepositories.collectLatest { repositories ->
                repositoryAdapter.submitList(repositories)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.featuredTopics.collectLatest { topics ->
                topicAdapter.submitList(topics)
            }
        }
    }

    private fun loadData() {
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 