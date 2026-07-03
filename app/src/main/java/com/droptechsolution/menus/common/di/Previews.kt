package com.droptechsolution.menus.common.di

import com.droptechsolution.shared.ui.home.views.DashboardHeaderRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.droptechsolution.menus.home.details.view.HomeScreen
import com.droptechsolution.menus.ui.theme._1MenusTheme
import com.droptechsolution.shared.ui.dashboard.DashboardScreen
import com.droptechsolution.shared.ui.login.SecureLoginScreen

class Previews {
}

@Preview
@Composable
fun SecureLoginScreenPreview() {
    homeScreenPreview()
}

@Composable
fun homeScreenPreview(){
    _1MenusTheme {
        DashboardScreen( modifier = Modifier.fillMaxWidth())
    }
}

@Composable
//@Preview
fun DashboardHeaderRowPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F7FB))

    ) {
        DashboardHeaderRow()
    }
}