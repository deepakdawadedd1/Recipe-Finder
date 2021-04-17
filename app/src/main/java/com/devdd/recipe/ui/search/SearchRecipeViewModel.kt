package com.devdd.recipe.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.devdd.recipe.data.prefs.manager.GuestManager
import com.devdd.recipe.data.remote.models.request.MarkRecipeFavoriteRequest
import com.devdd.recipe.domain.executers.MarkRecipeFavorite
import com.devdd.recipe.domain.observers.SearchRecipes
import com.devdd.recipe.domain.result.Event
import com.devdd.recipe.domain.result.InvokeStarted
import com.devdd.recipe.domain.viewstate.RecipeViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRecipeViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
    private val markRecipeFavorite: MarkRecipeFavorite,
    private val guestManager: GuestManager
) : ViewModel() {
    val query: MutableStateFlow<String> = MutableStateFlow("")

    private val mRecipes: MutableLiveData<List<RecipeViewState>> = MutableLiveData()
    val recipes: LiveData<List<RecipeViewState>>
        get() = mRecipes

    private val mNavigation: MutableLiveData<Event<NavDirections>> = MutableLiveData()
    val navigation: LiveData<Event<NavDirections>>
        get() = mNavigation

    private val mSavingRecipe: MutableLiveData<Pair<Boolean, Int>> = MutableLiveData()
    val savingRecipe: LiveData<Pair<Boolean, Int>>
        get() = mSavingRecipe

    init {
        viewModelScope.launch {
            query.debounce(300).filter { query ->
                if (query.isEmpty()) {
                    mRecipes.value = emptyList()
                    false
                } else true
            }.distinctUntilChanged().flatMapLatest {
                searchRecipes.invoke(it)
                searchRecipes.observe().catch { emit(emptyList()) }
            }.collect {
                mRecipes.postValue(it)
            }
        }
    }

    fun markRecipeFavorite(recipe: RecipeViewState) {
        viewModelScope.launch {
            markRecipeFavorite.invoke(
                MarkRecipeFavoriteRequest(
                    guestManager.guestToken(),
                    recipe.id,
                    !recipe.saved
                )
            )
                .collect {
                    mSavingRecipe.postValue(Pair(it is InvokeStarted, recipe.id))
                }
        }
    }

    fun navigateToRecipeDetails(viewState: RecipeViewState) {
        val navDirection = SearchRecipeFragmentDirections.actionToRecipeDetailFragment(viewState.id)
        mNavigation.value = Event(navDirection)
    }
}