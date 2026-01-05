package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

data class ShareContact(
    val name: String,
    val initials: String,
    var isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareLocationScreen(
    onBackClick: () -> Unit,
    onShareLocation: () -> Unit
) {
    var sharingDuration by remember { mutableStateOf("1 hour") }
    var expandedDuration by remember { mutableStateOf(false) }
    var isLiveSharing by remember { mutableStateOf(false) }
    
    val durations = listOf("15 minutes", "30 minutes", "1 hour", "2 hours", "Until I stop")
    
    val contacts = remember {
        mutableStateListOf(
            ShareContact("Sarah Miller", "SM", true),
            ShareContact("John Smith", "JS", false),
            ShareContact("Emily Davis", "ED", true),
            ShareContact("Alex Ray", "AR", false),
            ShareContact("Jessica Wu", "JW", false)
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Share Location",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Map Preview
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4F8))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Grid pattern
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(5) {
                                HorizontalDivider(color = Color(0xFFD0E8F0), thickness = 1.dp)
                            }
                        }
                        
                        // Current location marker
                        Box(
                            modifier = Modifier.align(Alignment.Center)
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
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(NavyBlue)
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                        
                        // Location text
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = White)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = CoralOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "123 Main Street, Lagos",
                                    fontSize = 13.sp,
                                    color = NavyBlue
                                )
                            }
                        }
                        
                        // Live indicator
                        if (isLiveSharing) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Circle,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "SHARING LIVE",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Duration Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Sharing Duration",
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
                        )
                        Text(
                            "How long should your location be shared?",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedDuration,
                            onExpandedChange = { expandedDuration = !expandedDuration }
                        ) {
                            OutlinedTextField(
                                value = sharingDuration,
                                onValueChange = { },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(Icons.Filled.Timer, contentDescription = null, tint = TextGray)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuration)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE8E8E8),
                                    focusedBorderColor = NavyBlue
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDuration,
                                onDismissRequest = { expandedDuration = false }
                            ) {
                                durations.forEach { duration ->
                                    DropdownMenuItem(
                                        text = { Text(duration) },
                                        onClick = {
                                            sharingDuration = duration
                                            expandedDuration = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Select Contacts
            item {
                Text(
                    "Share with",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    fontSize = 16.sp
                )
            }
            
            items(contacts) { contact ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val index = contacts.indexOf(contact)
                            contacts[index] = contact.copy(isSelected = !contact.isSelected)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (contact.isSelected) NavyBlue.copy(alpha = 0.05f) else White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (contact.isSelected) NavyBlue else Color(0xFFE8EAF6)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                contact.initials,
                                color = if (contact.isSelected) White else NavyBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            contact.name,
                            fontWeight = FontWeight.Medium,
                            color = NavyBlue,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Checkbox(
                            checked = contact.isSelected,
                            onCheckedChange = {
                                val index = contacts.indexOf(contact)
                                contacts[index] = contact.copy(isSelected = it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NavyBlue
                            )
                        )
                    }
                }
            }
            
            // Share Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                val selectedCount = contacts.count { it.isSelected }
                
                Button(
                    onClick = {
                        isLiveSharing = true
                        onShareLocation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    enabled = selectedCount > 0
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (selectedCount > 0) "Share with $selectedCount contact${if (selectedCount > 1) "s" else ""}"
                        else "Select contacts to share",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                if (isLiveSharing) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { isLiveSharing = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralOrange)
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Stop Sharing",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
