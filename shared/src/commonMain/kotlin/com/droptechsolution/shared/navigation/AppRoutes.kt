package com.droptechsolution.shared.navigation

import com.droptechsolution.shared.services.models.RequestActivityDto
import com.droptechsolution.shared.services.models.RequestDetails
import kotlinx.serialization.Serializable

/**
 * Root graph destinations — auth / onboarding flow and post-login shell.
 */
@Serializable
object LandingRoute

@Serializable
object SecureLoginRoute

@Serializable
object GuideRoute

@Serializable
object PartnerRoute

@Serializable
object DashboardRoute

/**
 * Nested dashboard graph destinations — managed by [DashboardScreen]'s inner NavHost.
 */
@Serializable
object HomeRoute

@Serializable
data class TasksRoute(
    val statusFilter: String? = null,
    val overviewCategory: String? = null,
)

@Serializable
data class TaskDetailRoute(
    val requestId: String,
    val source: String,
)

@Serializable
object ProfileRoute

@Serializable
object SettingsRoute
