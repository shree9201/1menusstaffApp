package com.droptechsolution.shared.ui.common.user

enum class StaffType(val apiKey: String) {
    FOMGR("FOMGR"),
    FOSU("FOSU"),
    FO("FO"),
    HKMGR("HKMGR"),
    HKSU("HKSU"),
    HK("HK"),
    MTNS("MTNS"),
    SPA("SPA"),
    WAITER_STAFF("WAITER-STAFF"),
    KITCHEN("KITCHEN"),
    UNKNOWN(""),
    ;

    fun defaultLabel(): String = when (this) {
        FOMGR -> "Front Office Manager"
        FOSU -> "Front Office Supervisor"
        FO -> "Front Office Executive"
        HKMGR -> "House Keeping Manager"
        HKSU -> "House Keeping Supervisor"
        HK -> "House Keeping Executive"
        MTNS -> "Maintenance"
        SPA -> "Spa"
        WAITER_STAFF -> "Waiter/Staff"
        KITCHEN -> "Kitchen"
        UNKNOWN -> "Staff"
    }

    companion object {
        fun fromApiKey(key: String?): StaffType =
            entries.firstOrNull { it.apiKey.equals(key, ignoreCase = true) } ?: UNKNOWN
    }
}

enum class StaffDepartment(val apiKey: String) {
    STAFF("STAFF"),
    MANAGER("MANAGER"),
    HR("HR"),
    UNKNOWN(""),
    ;

    fun defaultLabel(): String = when (this) {
        STAFF -> "Staff"
        MANAGER -> "Manager"
        HR -> "HR"
        UNKNOWN -> ""
    }

    /** Manager and HR roles see the Staff tab in bottom navigation. */
    fun showsStaffTab(): Boolean = this == MANAGER || this == HR

    companion object {
        fun fromApiKey(key: String?): StaffDepartment =
            when (key?.trim()?.uppercase()) {
                "HOD" -> MANAGER
                else -> entries.firstOrNull { it.apiKey.equals(key, ignoreCase = true) } ?: UNKNOWN
            }
    }
}

fun String.toStaffType(): StaffType = StaffType.fromApiKey(this)

fun String.toStaffDepartment(): StaffDepartment = StaffDepartment.fromApiKey(this)
