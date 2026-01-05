package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit,
    onEditProfile: () -> Unit,
    onGuardianContacts: () -> Unit,
    onSettings: () -> Unit,
    onSafetyTips: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = 3,
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
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyBlue)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "JD",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "John Doe",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    
                    Text(
                        "john.doe@email.com",
                        fontSize = 14.sp,
                        color = White.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Safety Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(value = "12", label = "Trips")
                        ProfileStat(value = "5", label = "Guardians")
                        ProfileStat(value = "28", label = "Safe Days")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quick Actions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Filled.Edit,
                        title = "Edit Profile",
                        subtitle = "Update your personal information",
                        onClick = onEditProfile
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileMenuItem(
                        icon = Icons.Filled.People,
                        title = "Guardian Contacts",
                        subtitle = "Manage emergency contacts",
                        onClick = onGuardianContacts
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileMenuItem(
                        icon = Icons.Filled.Security,
                        title = "Safety Tips",
                        subtitle = "Learn how to stay safe",
                        onClick = onSafetyTips
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Settings Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Filled.Settings,
                        title = "Settings",
                        subtitle = "App preferences and notifications",
                        onClick = onSettings
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileMenuItem(
                        icon = Icons.Filled.Help,
                        title = "Help & Support",
                        subtitle = "Get help or report issues",
                        onClick = { }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    ProfileMenuItem(
                        icon = Icons.Filled.Info,
                        title = "About GuardianX",
                        subtitle = "Version 1.0.0",
                        onClick = { }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Log Out",
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            label,
            fontSize = 12.sp,
            color = White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F4F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = NavyBlue,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue
            )
            Text(
                subtitle,
                fontSize = 13.sp,
                color = TextGray
            )
        }
        
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGray
        )
    }
}
