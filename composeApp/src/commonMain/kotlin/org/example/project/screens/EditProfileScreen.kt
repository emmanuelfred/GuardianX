package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveProfile: () -> Unit
) {
    var fullName by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@email.com") }
    var phoneNumber by remember { mutableStateOf("+234 801 234 5678") }
    var address by remember { mutableStateOf("123 Main Street, Lagos") }
    var emergencyMessage by remember { mutableStateOf("I need help! This is an emergency.") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Profile",
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
                    TextButton(onClick = onSaveProfile) {
                        Text(
                            "Save",
                            color = CoralOrange,
                            fontWeight = FontWeight.Bold
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
            // Profile Picture Section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(NavyBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "JD",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                    
                    // Camera icon overlay
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CoralOrange)
                            .align(Alignment.BottomEnd)
                            .clickable { /* Change photo */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Change photo",
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            TextButton(
                onClick = { /* Change photo */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Change Profile Photo", color = NavyBlue)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Personal Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Personal Information",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Full Name
                    ProfileTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        leadingIcon = Icons.Filled.Person
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email
                    ProfileTextField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = Icons.Filled.Email,
                        keyboardType = KeyboardType.Email
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Phone
                    ProfileTextField(
                        label = "Phone Number",
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        leadingIcon = Icons.Filled.Phone,
                        keyboardType = KeyboardType.Phone
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Address
                    ProfileTextField(
                        label = "Home Address",
                        value = address,
                        onValueChange = { address = it },
                        leadingIcon = Icons.Filled.Home
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Emergency Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Emergency Settings",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 16.sp
                    )
                    
                    Text(
                        "This message will be sent to your contacts during emergencies",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = emergencyMessage,
                        onValueChange = { emergencyMessage = it },
                        label = { Text("Emergency Message") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedBorderColor = NavyBlue
                        ),
                        maxLines = 4
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Medical Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.MedicalServices,
                            contentDescription = null,
                            tint = CoralOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Medical Information",
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            fontSize = 16.sp
                        )
                    }
                    
                    Text(
                        "Optional: Add medical info for emergencies",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { /* Add medical info */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Medical Information")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            Button(
                onClick = onSaveProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            color = NavyBlue,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = TextGray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE8E8E8),
                focusedBorderColor = NavyBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}
