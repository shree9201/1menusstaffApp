package com.droptechsolution.shared.ui.home.models

data class DashboardHeaderUi(
    val userName: String,

){
    var initials: String=""
    var role: String=""
    var status: String =""
    var hasUnreadNotifications: Boolean = false
}