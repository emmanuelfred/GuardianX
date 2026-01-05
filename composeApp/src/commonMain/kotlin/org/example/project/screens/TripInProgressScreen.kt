package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

data class TripUpdate(
    val icon: String,
    val title: String,
    val time: String,
    val isWarning: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripInProgressScreen(
    destination: String = "123 Safe St, Secure City",
    onBackClick: () -> Unit,
    onEndTrip: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(765) } // 00:12:45

    // Timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    val updates = listOf(
        TripUpdate("🚩", "Started Trip", "10:30 AM"),
        TripUpdate("📍", "Passing Oak Avenue", "10:35 AM"),
        TripUpdate("⚠️", "Deviated from route", "10:38 AM", isWarning = true)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Trip in Progress",
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
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More",
                            tint = NavyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
        ) {
            // Alert Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CoralOrange)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Help,
                        contentDescription = null,
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Are you okay?",
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            "You seem to have stopped for a while.",
                            fontSize = 14.sp,
                            color = White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFE8F4F8)),
                contentAlignment = Alignment.Center
            ) {
                Text("🗺️ Map View", color = TextGray)

                // Zoom Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp),
                        containerColor = White
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Zoom in", tint = NavyBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp),
                        containerColor = White
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Zoom out", tint = NavyBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp),
                        containerColor = White
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = "My location", tint = NavyBlue)
                    }
                }
            }

            // Timer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        timeString,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Text(
                        "Monitoring your trip...",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                    Text(
                        "To: $destination",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }

            // Live Updates
            Text(
                "Live Updates",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                updates.forEach { update ->
                    TripUpdateItem(update)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // End Trip Button
            Button(
                onClick = onEndTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
            ) {
                Text(
                    "End Trip Safely",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TripUpdateItem(update: TripUpdate) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(update.icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                update.title,
                fontWeight = FontWeight.Medium,
                color = if (update.isWarning) CoralOrange else NavyBlue
            )
            Text(
                update.time,
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}