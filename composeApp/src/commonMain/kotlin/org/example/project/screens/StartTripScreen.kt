package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

data class Guardian(
    val name: String,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTripScreen(
    onBackClick: () -> Unit,
    onStartTrip: () -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var destination by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var guardians by remember {
        mutableStateOf(
            listOf(
                Guardian("Jane Doe", true),
                Guardian("John Smith", false)
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Start a Trip",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = -1,
                onHomeClick = onHomeClick,
                onMapClick = onMapClick,
                onCommunityClick = onCommunityClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Destination
            Text(
                "Destination",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                placeholder = { Text("Enter your destination", color = TextGray) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = TextGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedBorderColor = NavyBlue,
                    unfocusedContainerColor = White,
                    focusedContainerColor = White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Estimated Time
            Text(
                "Estimated Time of Arrival",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = estimatedTime,
                onValueChange = { estimatedTime = it },
                placeholder = { Text("e.g., 30 minutes", color = TextGray) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = TextGray
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE8E8E8),
                    focusedBorderColor = NavyBlue,
                    unfocusedContainerColor = White,
                    focusedContainerColor = White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Share with Guardians
            Text(
                "Share with Guardians",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            guardians.forEachIndexed { index, guardian ->
                GuardianSelectItem(
                    name = guardian.name,
                    isSelected = guardian.isSelected,
                    onToggle = {
                        guardians = guardians.toMutableList().apply {
                            this[index] = guardian.copy(isSelected = !guardian.isSelected)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Add Guardian Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE8E8E8),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { /* TODO: Add guardian */ },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = NavyBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add another Guardian",
                        color = NavyBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Start Button
            Button(
                onClick = onStartTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Start Trip Monitoring",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GuardianSelectItem(
    name: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8E8E8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = TextGray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = NavyBlue,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NavyBlue else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) NavyBlue else Color(0xFFE8E8E8),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}