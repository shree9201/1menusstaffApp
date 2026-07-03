package com.droptechsolution.shared.navigation

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
object ProfileRoute

@Serializable
object SettingsRoute
