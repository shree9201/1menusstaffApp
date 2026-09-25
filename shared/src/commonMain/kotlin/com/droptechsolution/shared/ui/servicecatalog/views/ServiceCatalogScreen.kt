package com.droptechsolution.shared.ui.servicecatalog.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.ServiceCatalogItemUi
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogListViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceCatalogScreen(
    modifier: Modifier = Modifier,
    onServiceClick: (String) -> Unit = {},
    viewModel: ServiceCatalogListViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadServices()
    }

    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Services",
            color = BLACK,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Outlet service catalog",
            color = TextMuted,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading && services.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null && services.isEmpty() -> {
                Text(
                    text = errorMessage.orEmpty(),
                    color = TextMuted,
                    fontSize = 14.sp,
                )
            }
            else -> {
                services.forEach { service ->
                    ServiceCatalogCard(
                        service = service,
                        onClick = { onServiceClick(service.id) },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ServiceCatalogCard(
    service: ServiceCatalogItemUi,
    onClick: () -> Unit,
) {
    val isActive = service.status.equals("YES", ignoreCase = true)
    val badgeBackground = if (isActive) Color(0xFFDCFCE7) else Color(0xFFE5E7EB)
    val badgeTextColor = if (isActive) Color(0xFF15803D) else Color(0xFF6B7280)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.title,
                color = BLACK,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${service.category} · ${service.actionByLabel} · ${service.priority}",
                color = TextMuted,
                fontSize = 13.sp,
            )
        }
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(badgeBackground)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            text = if (isActive) "Active" else "Inactive",
            color = badgeTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
