package com.droptechsolution.shared.services.models

import kotlinx.serialization.Serializable

@Serializable
data class StatusCountDto(
    val status: String,
    val count: String,
)

@Serializable
data class ServiceStatusCountResponse(
    val status: Boolean,
    val value: String,
    val countByStatus: List<StatusCountDto> = emptyList(),
)

enum class DepartmentOverviewCategory {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    DELAYED,
}

data class DepartmentOverviewStatUi(
    val category: DepartmentOverviewCategory,
    val label: String,
    val count: Int,
    val filterStatuses: List<String>,
)

fun ServiceStatusCountResponse.toDepartmentOverviewStats(): List<DepartmentOverviewStatUi> {
    val countsByStatus = countByStatus.associate { item ->
        item.status.uppercase() to (item.count.toIntOrNull() ?: 0)
    }

    fun sum(vararg statuses: String): Int =
        statuses.sumOf { countsByStatus[it.uppercase()] ?: 0 }

    return listOf(
        DepartmentOverviewStatUi(
            category = DepartmentOverviewCategory.PENDING,
            label = "Pending",
            count = sum("NEW", "ASSIGN", "ASSIGNED"),
            filterStatuses = listOf("NEW"),
        ),
        DepartmentOverviewStatUi(
            category = DepartmentOverviewCategory.IN_PROGRESS,
            label = "In Progress",
            count = sum("ACCEPT", "ACCEPTED", "START", "STARTED"),
            filterStatuses = listOf("ACCEPT", "START"),
        ),
        DepartmentOverviewStatUi(
            category = DepartmentOverviewCategory.COMPLETED,
            label = "Completed",
            count = sum("CLOSE", "CLOSED", "DONE", "END"),
            filterStatuses = listOf("CLOSE"),
        ),
        DepartmentOverviewStatUi(
            category = DepartmentOverviewCategory.DELAYED,
            label = "Delayed",
            count = sum("ESCALATED", "HOLD", "PAUSE", "PAUSED", "REJECT", "REJECTED"),
            filterStatuses = listOf("ESCALATED"),
        ),
    )
}

fun DepartmentOverviewCategory.apiKey(): String = name
