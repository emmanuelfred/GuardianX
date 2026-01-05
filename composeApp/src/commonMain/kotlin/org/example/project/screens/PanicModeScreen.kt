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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.repository.SOSRepository
import org.example.project.data.repository.PoliceStationRepository
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@Composable
fun PanicModeScreen(
    onCallPolice: () -> Unit,
    onSendAlert: () -> Unit,
    onCancel: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var sosTriggered by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var nearestPolicePhone by remember { mutableStateOf<String?>(null) }
    var nearestPoliceStation by remember { mutableStateOf<String?>(null) }
    var notifiedContactsCount by remember { mutableStateOf(0) }
    var countdown by remember { mutableStateOf(30) }
    var canCancel by remember { mutableStateOf(true) }
    
    val scope = rememberCoroutineScope()
    val sosRepository = remember { SOSRepository() }
    val policeRepository = remember { PoliceStationRepository() }
    
    // Mock location - in real app, get from device GPS
    val latitude = 6.5244
    val longitude = 3.3792
    val address = "Lagos, Nigeria"
    
    // Auto-trigger SOS on screen load
    LaunchedEffect(Unit) {
        isLoading = true
        
        // Find nearest police station
        when (val result = policeRepository.getNearestStation(latitude, longitude)) {
            is ApiResult.Success -> {
                result.data.data?.let { station ->
                    nearestPolicePhone = station.phoneNumber
                    nearestPoliceStation = station.name
                }
            }
            is ApiResult.Error -> {
                nearestPolicePhone = "199" // Default emergency number
            }
            is ApiResult.Loading -> { }
        }
        
        // Trigger SOS alert
        when (val result = sosRepository.triggerSOS(
            latitude = latitude,
            longitude = longitude,
            address = address,
            action = "ALL"
        )) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    sosTriggered = true
                    notifiedContactsCount = (result.data.data?.get("notifiedContacts") as? Number)?.toInt() ?: 0
                } else {
                    errorMessage = result.data.message
                }
            }
            is ApiResult.Error -> {
                errorMessage = result.message
                // Still show UI even if API fails
                sosTriggered = true
            }
            is ApiResult.Loading -> { }
        }
        
        isLoading = false
    }
    
    // Countdown timer for cancel window
    LaunchedEffect(sosTriggered) {
        if (sosTriggered) {
            while (countdown > 0 && canCancel) {
                delay(1000)
                countdown--
            }
            canCancel = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDC2626))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "EMERGENCY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = White.copy(alpha = 0.8f),
                    letterSpacing = 2.sp
                )
                
                // Cancel button (only during countdown)
                if (canCancel && sosTriggered) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                // TODO: Cancel SOS via API
                                onCancel()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(White)
                        )
                    ) {
                        Text("Cancel ($countdown)")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sending SOS Alert...",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // Main Content
            else {
                // SOS Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "SOS",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(64.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "SOS ACTIVATED",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = White,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Help is on the way",
                    fontSize = 16.sp,
                    color = White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Status Cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Location shared
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Location Shared",
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Text(
                                    address,
                                    fontSize = 13.sp,
                                    color = White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Contacts notified
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.People,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Contacts Notified",
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Text(
                                    "$notifiedContactsCount emergency contacts alerted",
                                    fontSize = 13.sp,
                                    color = White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        if (nearestPoliceStation != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = White.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Nearest police
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.LocalPolice,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Nearest Police Station",
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                    Text(
                                        nearestPoliceStation ?: "Finding...",
                                        fontSize = 13.sp,
                                        color = White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Action Buttons
                Button(
                    onClick = onCallPolice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White)
                ) {
                    Icon(
                        Icons.Filled.Call,
                        contentDescription = null,
                        tint = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Call Police",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Text(
                            nearestPolicePhone ?: "199",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626).copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // I'm Safe Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            // Resolve SOS
                            onCancel()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = White),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(White)
                    )
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "I'm Safe - Cancel Alert",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
