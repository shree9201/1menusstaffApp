package com.droptechsolution.shared.ui.profile.models

data class ProfileUiState(
    val name: String = "",
    val roleLabel: String = "",
    val initials: String = "",
    val departmentLabel: String = "",
    val username: String = "",
    val teamScore: String = "—",
    val teamRating: String = "—",
    val deptPerformance: String = "—",
    val activeStaffCount: String = "—",
    val tasksTodayCount: String = "—",
    val isManagerProfile: Boolean = false,
)
