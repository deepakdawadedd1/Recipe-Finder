package com.devdd.recipe.ui.search

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devdd.recipe.R
import com.devdd.recipe.base.DevFragment
import com.devdd.recipe.databinding.FragmentSearchRecipeBinding
import com.devdd.recipe.ui.home.adapter.RecipeAdapter
import com.devdd.recipe.utils.extensions.observeEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchRecipeFragment :
    DevFragment<FragmentSearchRecipeBinding>(R.layout.fragment_search_recipe) {

    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    private val viewModel by viewModels<SearchRecipeViewModel>()

    private var recipeAdapter: RecipeAdapter? = null

    override fun onViewCreated(binding: FragmentSearchRecipeBinding, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        setViews()
        setupRecyclerViewAdapter()
        setObserver()

    }

    private fun setViews() {
        binding?.homeFragmentLottieNoRecipes?.setAnimation(R.raw.not_found_animation)
    }

    private fun setupRecyclerViewAdapter() {
        recipeAdapter = RecipeAdapter {
            viewModel.navigateToRecipeDetails(it)
        }
        binding?.searchRecipeFragmentSearchResults?.adapter = recipeAdapter
    }

    private fun setObserver() {
        viewModel.recipes.observe(viewLifecycleOwner) {
            recipeAdapter?.submitList(it)
        }
        viewModel.navigation.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recipeAdapter = null
    }
}