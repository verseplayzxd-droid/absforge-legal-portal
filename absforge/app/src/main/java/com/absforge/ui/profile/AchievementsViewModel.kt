package com.absforge.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.absforge.AbsForgeApplication
import com.absforge.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val achievements: List<AchievementEntity> = emptyList(),
    val isLoading: Boolean = true
)

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as AbsForgeApplication).database
    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        observeAchievements()
    }

    private fun observeAchievements() {
        viewModelScope.launch {
            db.achievementDao().getAll().collect { items ->
                _uiState.value = AchievementsUiState(
                    achievements = items,
                    isLoading = false
                )
            }
        }
    }
}
