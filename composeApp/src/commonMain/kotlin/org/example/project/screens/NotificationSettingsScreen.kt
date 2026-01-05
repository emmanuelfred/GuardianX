package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit
) {
    // SOS & Emergency
    var sosAlerts by remember { mutableStateOf(true) }
    var guardianAlerts by remember { mutableStateOf(true) }
    var emergencyUpdates by remember { mutableStateOf(true) }
    
    // Trip Monitoring
    var tripReminders by remember { mutableStateOf(true) }
    var checkInReminders by remember { mutableStateOf(true) }
    var routeDeviations by remember { mutableStateOf(true) }
    
    // Community
    var communityAlerts by remember { mutableStateOf(false) }
    var safetyTips by remember { mutableStateOf(true) }
    var nearbyDangers by remember { mutableStateOf(true) }
    
    // General
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notifications",
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // SOS & Emergency Section
            NotificationSection(
                title = "SOS & Emergency",
                icon = Icons.Filled.Warning,
                iconColor = CoralOrange
            ) {
                NotificationToggle(
                    title = "SOS Alerts",
                    subtitle = "Critical emergency notifications",
                    checked = sosAlerts,
                    onCheckedChange = { sosAlerts = it },
                    isImportant = true
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Guardian Alerts",
                    subtitle = "When guardians need help",
                    checked = guardianAlerts,
                    onCheckedChange = { guardianAlerts = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Emergency Updates",
                    subtitle = "Status updates during emergencies",
                    checked = emergencyUpdates,
                    onCheckedChange = { emergencyUpdates = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Trip Monitoring Section
            NotificationSection(
                title = "Trip Monitoring",
                icon = Icons.Filled.Route,
                iconColor = Color(0xFF22C55E)
            ) {
                NotificationToggle(
                    title = "Trip Reminders",
                    subtitle = "Start and end trip reminders",
                    checked = tripReminders,
                    onCheckedChange = { tripReminders = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Check-in Reminders",
                    subtitle = "Regular safety check-in prompts",
                    checked = checkInReminders,
                    onCheckedChange = { checkInReminders = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Route Deviations",
                    subtitle = "Alert when off planned route",
                    checked = routeDeviations,
                    onCheckedChange = { routeDeviations = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Community Section
            NotificationSection(
                title = "Community",
                icon = Icons.Filled.Groups,
                iconColor = Color(0xFF8B5CF6)
            ) {
                NotificationToggle(
                    title = "Community Alerts",
                    subtitle = "Posts and updates from community",
                    checked = communityAlerts,
                    onCheckedChange = { communityAlerts = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Safety Tips",
                    subtitle = "Daily safety tips and advice",
                    checked = safetyTips,
                    onCheckedChange = { safetyTips = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Nearby Danger Reports",
                    subtitle = "Hazards reported in your area",
                    checked = nearbyDangers,
                    onCheckedChange = { nearbyDangers = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sound & Vibration Section
            NotificationSection(
                title = "Sound & Vibration",
                icon = Icons.Filled.VolumeUp,
                iconColor = NavyBlue
            ) {
                NotificationToggle(
                    title = "Sound",
                    subtitle = "Play notification sounds",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                NotificationToggle(
                    title = "Vibration",
                    subtitle = "Vibrate for notifications",
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = NavyBlue
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "SOS alerts cannot be fully disabled for your safety. They will always notify you during emergencies.",
                        fontSize = 13.sp,
                        color = NavyBlue,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun NotificationSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = NavyBlue,
                fontSize = 14.sp
            )
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun NotificationToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isImportant: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    fontWeight = FontWeight.Medium,
                    color = NavyBlue
                )
                if (isImportant) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = CoralOrange)
                    ) {
                        Text(
                            "Important",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                subtitle,
                fontSize = 13.sp,
                color = TextGray
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = if (isImportant) CoralOrange else Color(0xFF22C55E)
            )
        )
    }
}
