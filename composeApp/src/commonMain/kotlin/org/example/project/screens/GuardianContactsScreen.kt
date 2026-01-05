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
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.EmergencyContactResponse
import org.example.project.data.repository.EmergencyContactRepository
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianContactsScreen(
    onBackClick: () -> Unit,
    onAddContact: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf<List<EmergencyContactResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<EmergencyContactResponse?>(null) }
    
    val scope = rememberCoroutineScope()
    val repository = remember { EmergencyContactRepository() }
    
    // Load contacts
    fun loadContacts() {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = repository.getContacts()) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        contacts = result.data.data ?: emptyList()
                    } else {
                        errorMessage = result.data.message
                        // Use sample data as fallback
                        contacts = getSampleContacts()
                    }
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                    // Use sample data as fallback
                    contacts = getSampleContacts()
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    // Delete contact
    fun deleteContact(contact: EmergencyContactResponse) {
        scope.launch {
            when (val result = repository.deleteContact(contact.id)) {
                is ApiResult.Success -> {
                    showDeleteDialog = null
                    loadContacts() // Refresh
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    // Toggle active status
    fun toggleActive(contact: EmergencyContactResponse) {
        scope.launch {
            when (val result = repository.toggleActive(contact.id)) {
                is ApiResult.Success -> {
                    loadContacts() // Refresh
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
        }
    }
    
    // Initial load
    LaunchedEffect(Unit) {
        loadContacts()
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { contact ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = CoralOrange,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Delete Contact?", fontWeight = FontWeight.Bold, color = NavyBlue)
            },
            text = {
                Text(
                    "Are you sure you want to remove ${contact.name} from your emergency contacts?",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { deleteContact(contact) },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Guardian Contacts",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddContact,
                containerColor = NavyBlue,
                contentColor = White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search contacts", color = TextGray) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = TextGray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextGray)
                        }
                    }
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

            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${contacts.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text("Total", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${contacts.count { it.isActive }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                        Text("Active", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${contacts.count { it.notifyBySms || it.notifyByEmail }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralOrange
                        )
                        Text("Will Notify", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Loading State
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NavyBlue)
                }
            }
            // Contacts List
            else {
                val filteredContacts = contacts.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery)
                }
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredContacts.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.SearchOff,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No contacts found", color = TextGray)
                                }
                            }
                        }
                    } else if (filteredContacts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.PersonAdd,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No emergency contacts yet", color = TextGray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Tap + to add your first contact",
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredContacts) { contact ->
                            ContactCard(
                                contact = contact,
                                onToggleActive = { toggleActive(contact) },
                                onDelete = { showDeleteDialog = contact }
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // FAB space
                    }
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: EmergencyContactResponse,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (contact.isActive) White else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (contact.isActive) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (contact.isActive) Color(0xFFE8EAF6) else Color(0xFFE0E0E0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    contact.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString(""),
                    color = if (contact.isActive) NavyBlue else TextGray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        contact.name,
                        fontWeight = FontWeight.SemiBold,
                        color = if (contact.isActive) NavyBlue else TextGray,
                        fontSize = 16.sp
                    )
                    if (!contact.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                        ) {
                            Text(
                                "Inactive",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = TextGray
                            )
                        }
                    }
                }
                
                Text(
                    contact.phoneNumber,
                    fontSize = 13.sp,
                    color = TextGray
                )
                
                contact.relationship?.let { relationship ->
                    Text(
                        relationship,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                
                // Notification methods
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (contact.notifyBySms) {
                        Icon(
                            Icons.Filled.Sms,
                            contentDescription = "SMS enabled",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (contact.notifyByEmail) {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = "Email enabled",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (contact.notifyByCall) {
                        Icon(
                            Icons.Filled.Call,
                            contentDescription = "Call enabled",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Options",
                        tint = TextGray
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { 
                            Text(if (contact.isActive) "Deactivate" else "Activate")
                        },
                        leadingIcon = {
                            Icon(
                                if (contact.isActive) Icons.Filled.ToggleOff else Icons.Filled.ToggleOn,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleActive()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = CoralOrange) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = null,
                                tint = CoralOrange
                            )
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

// Sample contacts for fallback
fun getSampleContacts(): List<EmergencyContactResponse> {
    return listOf(
        EmergencyContactResponse(
            id = "1",
            userId = "user1",
            name = "Sarah Miller",
            phoneNumber = "+234 801 234 5678",
            email = "sarah@email.com",
            relationship = "Mother",
            isActive = true,
            notifyBySms = true,
            notifyByEmail = true,
            notifyByCall = false,
            priority = 1
        ),
        EmergencyContactResponse(
            id = "2",
            userId = "user1",
            name = "John Smith",
            phoneNumber = "+234 802 345 6789",
            email = "john@email.com",
            relationship = "Friend",
            isActive = true,
            notifyBySms = true,
            notifyByEmail = false,
            notifyByCall = true,
            priority = 2
        ),
        EmergencyContactResponse(
            id = "3",
            userId = "user1",
            name = "Emily Davis",
            phoneNumber = "+234 803 456 7890",
            email = null,
            relationship = "Sister",
            isActive = false,
            notifyBySms = true,
            notifyByEmail = false,
            notifyByCall = false,
            priority = 3
        )
    )
}
