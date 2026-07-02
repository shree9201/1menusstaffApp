package com.droptechsolution.shared.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass

/**
 * Allows retrieving any [ViewModel] from Swift code. Swift passes [ObjCClass] because
 * Kotlin/Swift interop does not preserve generic types; [getOriginalKotlinClass]
 * recovers the original [KClass] on the Kotlin side.
 */
@OptIn(BetaInteropApi::class)
@Throws(IllegalArgumentException::class)
fun ViewModelStore.resolveViewModel(
    modelClass: ObjCClass,
    factory: ViewModelProvider.Factory,
    key: String? = null,
    extras: CreationExtras? = null,
): ViewModel {
    @Suppress("UNCHECKED_CAST")
    val vmClass = getOriginalKotlinClass(modelClass) as? KClass<ViewModel>
    require(vmClass != null) { "The modelClass parameter must be a ViewModel type." }

    val provider = ViewModelProvider.create(this, factory, extras ?: CreationExtras.Empty)
    return key?.let { provider[it, vmClass] } ?: provider[vmClass]
}
