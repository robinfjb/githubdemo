package com.example.githubdemo.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubdemo.GitHubApp
import com.example.githubdemo.R
import com.example.githubdemo.databinding.FragmentSearchBinding
import com.example.githubdemo.ui.repository.RepositoryDetailFragment
import com.example.githubdemo.ui.search.adapter.RepositoryAdapter
import com.google.android.material.chip.Chip

/**
 * 搜索Fragment
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var repositoryAdapter: RepositoryAdapter
    
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory((requireActivity().application as GitHubApp).appContainer.gitHubRepository)
    }
    
    // 是否正在加载更多数据
    private var isLoadingMore = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchInput()
        setupLanguageChips()
        observeViewModel()
    }

    private fun setupRecyclerView() {
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
        
        binding.searchResultsRecyclerView.adapter = repositoryAdapter
        
        // 添加滚动监听器，实现加载更多功能
        binding.searchResultsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                
                // 判断是否滑动到了底部，需要加载更多数据
                if (!isLoadingMore && 
                    viewModel.hasMoreData.value == true && 
                    !viewModel.isLoading.value!! &&
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 && 
                    firstVisibleItemPosition >= 0) {
                    
                    loadMoreResults()
                }
            }
        })
    }
    
    private fun loadMoreResults() {
        isLoadingMore = true
        binding.loadMoreProgressBar.visibility = View.VISIBLE
        viewModel.loadMoreResults()
    }

    private fun setupSearchInput() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }
        
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()) {
                viewModel.clearSearchResults()
                binding.languageChipGroup.clearCheck()
            }
        }
    }

    private fun setupLanguageChips() {
        binding.languageChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = group.findViewById<Chip>(checkedIds.first())
                val language = chip.text.toString()
                viewModel.searchByLanguage(language)
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchRepositories(query)
        }
    }

    private fun observeViewModel() {
        // 观察搜索结果
        viewModel.searchResults.observe(viewLifecycleOwner) { repositories ->
            repositoryAdapter.submitList(repositories)
            binding.resultsTitle.visibility = if (repositories.isNotEmpty()) View.VISIBLE else View.GONE
            binding.emptyResultsTextView.visibility = if (repositories.isEmpty() && !viewModel.isLoading.value!!) View.VISIBLE else View.GONE
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading && !isLoadingMore) View.VISIBLE else View.GONE
            
            if (!isLoading) {
                // 重置加载更多状态
                isLoadingMore = false
                binding.loadMoreProgressBar.visibility = View.GONE
                
                if (viewModel.searchResults.value?.isEmpty() == true) {
                    binding.emptyResultsTextView.visibility = View.VISIBLE
                }
            } else if (!isLoadingMore) {
                binding.emptyResultsTextView.visibility = View.GONE
            }
        }
        
        // 观察是否还有更多数据
        viewModel.hasMoreData.observe(viewLifecycleOwner) { hasMoreData ->
            binding.noMoreDataTextView.visibility = if (!hasMoreData && viewModel.searchResults.value?.isNotEmpty() == true) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 