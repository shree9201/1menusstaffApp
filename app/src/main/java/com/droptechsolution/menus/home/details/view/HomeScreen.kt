package com.droptechsolution.menus.home.details.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.droptechsolution.menus.home.HomeViewModel
import com.droptechsolution.shared.outletinfo.model.api.staff.StaffDetails

@Composable
fun HomeScreen(
    modifier: Modifier,
    homeViewModel: HomeViewModel
) {
    var titleText by remember {
        mutableStateOf("")
    }

    var messageText by remember {
        mutableStateOf("")
    }
    var staffId: String = ""

    val staffList by homeViewModel.staffs.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(), // Ensure the column spans the full width
    ) {

        Box(modifier = modifier.padding(16.dp).align(Alignment.CenterHorizontally)) {
            StaffDropdown(staffList = staffList, onStaffSelected = { staff ->
                staffId = staff.id
            })
        }
        TextField(
            value = titleText,
            onValueChange = { titleText = it },
            maxLines = 2,
            placeholder = { Text("") },
            textStyle = TextStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            ),
            label = { Text("Enter Title") },
            modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)
        )
        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            maxLines = 2,
            placeholder = { Text("") },
            textStyle = TextStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            ),
            label = { Text("Enter Message") },
            modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)
        )
        Button(onClick = {
            homeViewModel?.sendNotification(staffId,titleText,messageText)
        },
            modifier = Modifier.align(Alignment.CenterHorizontally)
            ){
            Text("Send Notification")

        }
        
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDropdown(
    staffList: List<StaffDetails>,
    onStaffSelected: (StaffDetails) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedStaff by remember {
        mutableStateOf<StaffDetails?>(null)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        TextField(
            value = selectedStaff?.name ?: "Select Staff",
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Staff")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            staffList.forEach { staff ->

                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = staff.name ?: ""
                            )

                            Text(
                                text = staff.type ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {

                        selectedStaff = staff
                        expanded = false

                        onStaffSelected(staff)
                    }
                )
            }
        }
    }
}