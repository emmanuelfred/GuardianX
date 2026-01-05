package org.example.project.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

data class SafetyTip(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

data class SafetyCategory(
    val title: String,
    val tips: List<SafetyTip>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyTipsScreen(
    onBackClick: () -> Unit
) {
    val categories = listOf(
        SafetyCategory(
            title = "🚗 Travel Safety",
            tips = listOf(
                SafetyTip(
                    Icons.Filled.Route,
                    "Plan Your Route",
                    "Always share your travel plans with trusted contacts before leaving. Use well-lit and populated routes.",
                    Color(0xFF22C55E)
                ),
                SafetyTip(
                    Icons.Filled.DirectionsCar,
                    "Ride-Share Safety",
                    "Verify driver details and license plate before entering. Share ride details with friends or family.",
                    Color(0xFF3B82F6)
                ),
                SafetyTip(
                    Icons.Filled.NightsStay,
                    "Night Travel",
                    "Avoid traveling alone at night. If unavoidable, stay in well-lit areas and remain alert.",
                    Color(0xFF8B5CF6)
                )
            )
        ),
        SafetyCategory(
            title = "📱 Digital Safety",
            tips = listOf(
                SafetyTip(
                    Icons.Filled.LocationOn,
                    "Location Sharing",
                    "Enable live location sharing with trusted contacts during trips or when meeting strangers.",
                    CoralOrange
                ),
                SafetyTip(
                    Icons.Filled.Battery5Bar,
                    "Keep Phone Charged",
                    "Always ensure your phone has sufficient battery. Carry a power bank for emergencies.",
                    Color(0xFFEAB308)
                ),
                SafetyTip(
                    Icons.Filled.ContactPhone,
                    "Emergency Contacts",
                    "Keep emergency numbers saved and easily accessible. Know your local emergency services.",
                    Color(0xFFEC4899)
                )
            )
        ),
        SafetyCategory(
            title = "🏠 Personal Safety",
            tips = listOf(
                SafetyTip(
                    Icons.Filled.Visibility,
                    "Stay Aware",
                    "Always be aware of your surroundings. Avoid distractions like headphones in unfamiliar areas.",
                    Color(0xFF14B8A6)
                ),
                SafetyTip(
                    Icons.Filled.Groups,
                    "Trust Your Instincts",
                    "If something feels wrong, trust your gut. Remove yourself from uncomfortable situations.",
                    Color(0xFFF97316)
                ),
                SafetyTip(
                    Icons.Filled.Key,
                    "Secure Your Home",
                    "Always lock doors and windows. Don't share your location or daily routine on social media.",
                    NavyBlue
                )
            )
        ),
        SafetyCategory(
            title = "🚨 Emergency Response",
            tips = listOf(
                SafetyTip(
                    Icons.Filled.Warning,
                    "Use SOS Feature",
                    "Familiarize yourself with GuardianX SOS feature. Practice using it so you're prepared.",
                    Color(0xFFDC2626)
                ),
                SafetyTip(
                    Icons.Filled.LocalPolice,
                    "Know Your Area",
                    "Know the location of nearest police stations, hospitals, and safe zones in your area.",
                    Color(0xFF1E40AF)
                ),
                SafetyTip(
                    Icons.Filled.RecordVoiceOver,
                    "Make Noise",
                    "In dangerous situations, make as much noise as possible to attract attention and deter attackers.",
                    Color(0xFF7C3AED)
                )
            )
        )
    )
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Safety Tips",
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
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Shield,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Stay Safe",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                "Essential tips to keep you protected",
                                fontSize = 14.sp,
                                color = White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            // Categories
            categories.forEach { category ->
                item {
                    Text(
                        category.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NavyBlue,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(category.tips) { tip ->
                    SafetyTipCard(tip)
                }
            }
            
            // Footer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CoralOrange.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = CoralOrange
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Remember: Your safety is the priority. When in doubt, always err on the side of caution.",
                            fontSize = 14.sp,
                            color = NavyBlue
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SafetyTipCard(tip: SafetyTip) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(tip.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tip.icon,
                        contentDescription = null,
                        tint = tip.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    tip.title,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyBlue,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = TextGray
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    tip.description,
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
