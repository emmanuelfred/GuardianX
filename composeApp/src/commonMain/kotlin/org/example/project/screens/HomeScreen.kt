package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@Composable
fun HomeScreen(
    onSOSClick: () -> Unit,
    onShareLocationClick: () -> Unit,
    onReportDangerClick: () -> Unit,
    onStartTripClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var isSOSHeld by remember { mutableStateOf(false) }
    var sosProgress by remember { mutableStateOf(0f) }
    var safetyStatus by remember { mutableStateOf("safe") } // safe, monitoring, alert
    val scope = rememberCoroutineScope()
    
    // SOS Hold Timer
    LaunchedEffect(isSOSHeld) {
        if (isSOSHeld) {
            sosProgress = 0f
            while (isSOSHeld && sosProgress < 1f) {
                delay(50)
                sosProgress += 0.05f
            }
            if (sosProgress >= 1f) {
                onSOSClick()
            }
        } else {
            sosProgress = 0f
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GuardianX",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Text(
                        text = "Stay safe, stay connected",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                // Profile Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8))
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = NavyBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Safety Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (safetyStatus) {
                        "alert" -> CoralOrange.copy(alpha = 0.1f)
                        "monitoring" -> Color(0xFFFFF7ED)
                        else -> White
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = "Safe",
                        tint = when (safetyStatus) {
                            "alert" -> CoralOrange
                            "monitoring" -> Color(0xFFF59E0B)
                            else -> Color(0xFF22C55E)
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Safety Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyBlue
                        )
                        Text(
                            text = when (safetyStatus) {
                                "alert" -> "Alert Active!"
                                "monitoring" -> "Trip in Progress"
                                else -> "You are Safe"
                            },
                            fontSize = 14.sp,
                            color = when (safetyStatus) {
                                "alert" -> CoralOrange
                                "monitoring" -> Color(0xFFF59E0B)
                                else -> Color(0xFF22C55E)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SOS Button with hold-to-activate
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                // Progress ring
                if (sosProgress > 0) {
                    CircularProgressIndicator(
                        progress = { sosProgress },
                        modifier = Modifier.size(190.dp),
                        color = Color(0xFFDC2626),
                        strokeWidth = 4.dp,
                        trackColor = Color.Transparent
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .shadow(20.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isSOSHeld) {
                                    listOf(Color(0xFFDC2626), Color(0xFFB91C1C))
                                } else {
                                    listOf(Color(0xFFFF8A7A), CoralOrange)
                                }
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isSOSHeld = true
                                    tryAwaitRelease()
                                    isSOSHeld = false
                                },
                                onTap = {
                                    // Quick tap - show instruction
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = White
                        )
                        Text(
                            text = if (isSOSHeld) "HOLD..." else "HOLD FOR SOS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Action Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Share Location
                QuickActionCard(
                    icon = Icons.Filled.LocationOn,
                    iconBackgroundColor = Color(0xFFE8F4FC),
                    iconTint = NavyBlue,
                    title = "Share Location",
                    subtitle = "Live location sharing",
                    modifier = Modifier.weight(1f),
                    onClick = onShareLocationClick
                )

                // Report Danger
                QuickActionCard(
                    icon = Icons.Filled.Warning,
                    iconBackgroundColor = Color(0xFFFFF0ED),
                    iconTint = CoralOrange,
                    title = "Report Danger",
                    subtitle = "Alert others to hazards",
                    modifier = Modifier.weight(1f),
                    onClick = onReportDangerClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Trip Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Filled.Route,
                    iconBackgroundColor = Color(0xFFE8F8F0),
                    iconTint = Color(0xFF22C55E),
                    title = "Start a Trip",
                    subtitle = "Monitor your journey",
                    modifier = Modifier.weight(1f),
                    onClick = onStartTripClick
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Empty space for balance
                Box(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Navigation
            BottomNavBar(
                selectedTab = 0,
                onHomeClick = onHomeClick,
                onMapClick = onMapClick,
                onCommunityClick = onCommunityClick,
                onProfileClick = onSettingsClick
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}
