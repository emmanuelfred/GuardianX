package org.example.project.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchLocation: () -> Unit = {},
    onSOSClick: () -> Unit = {}
) {
    var showSafeZones by remember { mutableStateOf(true) }
    var showPoliceStations by remember { mutableStateOf(true) }
    var showHospitals by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = 1,
                onHomeClick = onHomeClick,
                onMapClick = onMapClick,
                onCommunityClick = onCommunityClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map Placeholder - In production, replace with actual map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8F4F8))
            ) {
                // Grid pattern to simulate map
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(8) {
                        Divider(color = Color(0xFFD0E8F0), thickness = 1.dp)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(6) {
                        VerticalDivider(color = Color(0xFFD0E8F0), thickness = 1.dp)
                    }
                }
                
                // Map label
                Text(
                    "🗺️ Map View",
                    modifier = Modifier.align(Alignment.Center),
                    color = TextGray,
                    fontSize = 18.sp
                )
                
                // Current location marker
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(NavyBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(NavyBlue)
                        )
                    }
                }
                
                // Sample markers for police stations
                if (showPoliceStations) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 120.dp, end = 60.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocalPolice,
                            contentDescription = "Police Station",
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 200.dp, start = 40.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocalPolice,
                            contentDescription = "Police Station",
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                // Sample safe zones
                if (showSafeZones) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 180.dp, start = 80.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Shield,
                                contentDescription = "Safe Zone",
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            
            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextGray
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Search location...",
                        color = TextGray
                    )
                }
            }
            
            // Map Controls
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                FloatingActionButton(
                    onClick = { /* Zoom in */ },
                    modifier = Modifier.size(40.dp),
                    containerColor = White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Zoom in", tint = NavyBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                FloatingActionButton(
                    onClick = { /* Zoom out */ },
                    modifier = Modifier.size(40.dp),
                    containerColor = White
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Zoom out", tint = NavyBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                FloatingActionButton(
                    onClick = { /* Center on location */ },
                    modifier = Modifier.size(40.dp),
                    containerColor = White
                ) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "My location", tint = NavyBlue)
                }
            }
            
            // Filter Chips
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { showSafeZones = !showSafeZones },
                        label = { Text("Safe Zones", fontSize = 12.sp) },
                        selected = showSafeZones,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF22C55E).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFF22C55E)
                        )
                    )
                    FilterChip(
                        onClick = { showPoliceStations = !showPoliceStations },
                        label = { Text("Police", fontSize = 12.sp) },
                        selected = showPoliceStations,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LocalPolice,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1E40AF).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFF1E40AF)
                        )
                    )
                    FilterChip(
                        onClick = { showHospitals = !showHospitals },
                        label = { Text("Hospitals", fontSize = 12.sp) },
                        selected = showHospitals,
                        leadingIcon = {
                            Icon(
                                Icons.Filled.LocalHospital,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CoralOrange.copy(alpha = 0.2f),
                            selectedLabelColor = CoralOrange
                        )
                    )
                }
            }
            
            // Bottom Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = CoralOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                "Current Location",
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue
                            )
                            Text(
                                "123 Main Street, Lagos, Nigeria",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { /* Share location */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share")
                        }
                        
                        Button(
                            onClick = onSOSClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SOS")
                        }
                    }
                }
            }
        }
    }
}
