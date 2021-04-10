package com.devdd.recipe.ui.preferencesetting.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.devdd.recipe.R
import com.devdd.recipe.data.prefs.manager.LocaleManager
import com.devdd.recipe.data.prefs.manager.RecipeManager
import com.devdd.recipe.domain.result.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguagePreferenceViewModel @Inject constructor(
    private val localeManager: LocaleManager,
    private val recipeManager: RecipeManager
) : ViewModel() {
    val checkButtonId: MutableLiveData<Int> = MutableLiveData()

    private val mNavigation: MutableLiveData<Event<NavDirections>> = MutableLiveData()
    val navigation: LiveData<Event<NavDirections>>
        get() = mNavigation

    init {
        loadLanguagePreference()
    }

    private fun loadLanguagePreference() {
        viewModelScope.launch {
            with(localeManager) {
                val id = when {
                    isEnglishLocale() -> LanguageOptionId.ENGLISH
                    isHindiLocale() -> LanguageOptionId.HINDI
                    else -> return@with
                }
                checkButtonId.postValue(id)
            }
        }
    }

    fun english() {
        checkButtonId.value = LanguageOptionId.ENGLISH
        updateDataStore(LocaleManager.LOCALE_ENGLISH)
    }

    fun hindi() {
        checkButtonId.value = LanguageOptionId.HINDI
        updateDataStore(LocaleManager.LOCALE_HINDI)
    }

    private fun updateDataStore(language: String) {
        viewModelScope.launch {
            localeManager.updateLanguage(language)
            if (recipeManager.isRecipeSelected())
                navigateToHome()
            else navigateToRecipePref()
        }
    }

    private fun navigateToHome() {
        val direction = LanguagePreferenceFragmentDirections.actionToHomeFragment()
        mNavigation.value = Event(direction)
    }

    private fun navigateToRecipePref() {
        val direction = LanguagePreferenceFragmentDirections.actionToRecipePreferenceFragment()
        mNavigation.value = Event(direction)

    }

    private object LanguageOptionId {
        const val ENGLISH = R.id.language_preference_fragment_option_english
        const val HINDI = R.id.language_preference_fragment_option_hindi
    }

}