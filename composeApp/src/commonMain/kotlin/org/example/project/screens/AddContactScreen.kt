package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.repository.EmergencyContactRepository
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(
    onBackClick: () -> Unit,
    onSaveContact: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var notifyBySms by remember { mutableStateOf(true) }
    var notifyByEmail by remember { mutableStateOf(true) }
    var notifyByCall by remember { mutableStateOf(false) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedRelationship by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val repository = remember { EmergencyContactRepository() }
    
    val relationships = listOf("Father", "Mother", "Spouse", "Sibling", "Friend", "Colleague", "Other")
    
    fun validateAndSave() {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = null
        }
        
        if (phoneNumber.isBlank()) {
            phoneError = "Phone number is required"
            isValid = false
        } else if (phoneNumber.length < 10) {
            phoneError = "Enter a valid phone number"
            isValid = false
        } else {
            phoneError = null
        }
        
        if (isValid) {
            scope.launch {
                isLoading = true
                errorMessage = null
                
                when (val result = repository.addContact(
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email.ifBlank { null },
                    relationship = relationship.ifBlank { null },
                    notifyBySms = notifyBySms,
                    notifyByEmail = notifyByEmail,
                    notifyByCall = notifyByCall
                )) {
                    is ApiResult.Success -> {
                        if (result.data.success) {
                            onSaveContact()
                        } else {
                            errorMessage = result.data.message
                        }
                    }
                    is ApiResult.Error -> {
                        errorMessage = result.message
                    }
                    is ApiResult.Loading -> { }
                }
                
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Contact", fontWeight = FontWeight.Bold, color = NavyBlue) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = White)
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
            // Avatar
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.Center
                ) {
                    if (name.isNotBlank()) {
                        Text(
                            name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString(""),
                            fontSize = 32.sp, fontWeight = FontWeight.Bold, color = NavyBlue
                        )
                    } else {
                        Icon(Icons.Filled.PersonAdd, null, tint = NavyBlue, modifier = Modifier.size(48.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Error, null, tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(error, color = Color(0xFFDC2626), fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Full Name *", fontWeight = FontWeight.Medium, color = NavyBlue, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = null },
                        placeholder = { Text("Enter full name", color = TextGray) },
                        leadingIcon = { Icon(Icons.Filled.Person, null, tint = TextGray) },
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it, color = Color(0xFFDC2626)) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedBorderColor = NavyBlue
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Phone Number *", fontWeight = FontWeight.Medium, color = NavyBlue, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it; phoneError = null },
                        placeholder = { Text("+234 800 000 0000", color = TextGray) },
                        leadingIcon = { Icon(Icons.Filled.Phone, null, tint = TextGray) },
                        isError = phoneError != null,
                        supportingText = phoneError?.let { { Text(it, color = Color(0xFFDC2626)) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedBorderColor = NavyBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Email (Optional)", fontWeight = FontWeight.Medium, color = NavyBlue, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("contact@email.com", color = TextGray) },
                        leadingIcon = { Icon(Icons.Filled.Email, null, tint = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE8E8E8),
                            focusedBorderColor = NavyBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        enabled = !isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Relationship", fontWeight = FontWeight.Medium, color = NavyBlue, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedRelationship,
                        onExpandedChange = { if (!isLoading) expandedRelationship = !expandedRelationship }
                    ) {
                        OutlinedTextField(
                            value = relationship,
                            onValueChange = { },
                            readOnly = true,
                            placeholder = { Text("Select relationship", color = TextGray) },
                            leadingIcon = { Icon(Icons.Filled.People, null, tint = TextGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRelationship) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE8E8E8),
                                focusedBorderColor = NavyBlue
                            ),
                            enabled = !isLoading
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRelationship,
                            onDismissRequest = { expandedRelationship = false }
                        ) {
                            relationships.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { relationship = option; expandedRelationship = false }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notification Preferences
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Notification Preferences", fontWeight = FontWeight.Bold, color = NavyBlue)
                    Text("How should we notify this contact?", fontSize = 13.sp, color = TextGray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // SMS
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F4F8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Sms, null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SMS Notification", fontWeight = FontWeight.Medium, color = NavyBlue)
                            Text("Send text message alerts", fontSize = 12.sp, color = TextGray)
                        }
                        Switch(
                            checked = notifyBySms,
                            onCheckedChange = { notifyBySms = it },
                            enabled = !isLoading,
                            colors = SwitchDefaults.colors(checkedThumbColor = White, checkedTrackColor = Color(0xFF22C55E))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Email
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F4F8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Email, null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Email Notification", fontWeight = FontWeight.Medium, color = NavyBlue)
                            Text("Send email with location", fontSize = 12.sp, color = TextGray)
                        }
                        Switch(
                            checked = notifyByEmail,
                            onCheckedChange = { notifyByEmail = it },
                            enabled = !isLoading,
                            colors = SwitchDefaults.colors(checkedThumbColor = White, checkedTrackColor = Color(0xFF22C55E))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Call
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0F4F8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Call, null, tint = NavyBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Phone Call", fontWeight = FontWeight.Medium, color = NavyBlue)
                            Text("Automated emergency call", fontSize = 12.sp, color = TextGray)
                        }
                        Switch(
                            checked = notifyByCall,
                            onCheckedChange = { notifyByCall = it },
                            enabled = !isLoading,
                            colors = SwitchDefaults.colors(checkedThumbColor = White, checkedTrackColor = Color(0xFF22C55E))
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            Button(
                onClick = { validateAndSave() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                enabled = !isLoading && name.isNotBlank() && phoneNumber.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Contact", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
