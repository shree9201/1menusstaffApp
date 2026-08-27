package com.droptechsolution.shared.ui.profile.updateprofile.models

data class UpdateProfileUiState(
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val username: String = "",
    val customisedPosition: String = "",
    val isLoading: Boolean = false,
    val isLoaded: Boolean = false,
)
