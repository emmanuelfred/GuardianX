package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import org.example.project.data.api.ApiResult
import org.example.project.data.models.PostResponse
import org.example.project.data.repository.CommunityRepository
import org.example.project.theme.CoralOrange
import org.example.project.theme.NavyBlue
import org.example.project.theme.TextGray
import org.example.project.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("All Posts") }
    val tabs = listOf("All Posts", "Safety Alerts", "Tips", "Local News")
    
    // State
    var posts by remember { mutableStateOf<List<PostResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val repository = remember { CommunityRepository() }
    
    // Load posts
    fun loadPosts(category: String? = null) {
        scope.launch {
            isLoading = true
            errorMessage = null
            
            val categoryParam = when (selectedTab) {
                "Safety Alerts" -> "SAFETY_ALERT"
                "Tips" -> "SAFETY_TIP"
                "Local News" -> "LOCAL_NEWS"
                else -> null
            }
            
            when (val result = repository.getPosts(category = categoryParam)) {
                is ApiResult.Success -> {
                    posts = result.data.data
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                    // Use sample data as fallback
                    posts = getSamplePosts()
                }
                is ApiResult.Loading -> { }
            }
            
            isLoading = false
        }
    }
    
    // Create post
    fun createPost() {
        if (newPostContent.isBlank()) return
        
        scope.launch {
            isPosting = true
            
            val category = when (selectedTab) {
                "Safety Alerts" -> "SAFETY_ALERT"
                "Tips" -> "SAFETY_TIP"
                "Local News" -> "LOCAL_NEWS"
                else -> "GENERAL"
            }
            
            when (val result = repository.createPost(newPostContent, category)) {
                is ApiResult.Success -> {
                    newPostContent = ""
                    showCreatePostDialog = false
                    loadPosts() // Refresh
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                is ApiResult.Loading -> { }
            }
            
            isPosting = false
        }
    }
    
    // Like/Unlike post
    fun toggleLike(post: PostResponse) {
        scope.launch {
            if (post.isLikedByCurrentUser) {
                repository.unlikePost(post.id)
            } else {
                repository.likePost(post.id)
            }
            loadPosts() // Refresh
        }
    }
    
    // Initial load
    LaunchedEffect(selectedTab) {
        loadPosts()
    }
    
    // Create Post Dialog
    if (showCreatePostDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = {
                Text("Create Post", fontWeight = FontWeight.Bold, color = NavyBlue)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPostContent,
                        onValueChange = { newPostContent = it },
                        placeholder = { Text("Share a safety tip or alert...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6,
                        enabled = !isPosting
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Category: ${selectedTab}",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { createPost() },
                    enabled = newPostContent.isNotBlank() && !isPosting,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    if (isPosting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Post")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }, enabled = !isPosting) {
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
                        "Community",
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = NavyBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = White
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = 2,
                onHomeClick = onHomeClick,
                onMapClick = onMapClick,
                onCommunityClick = onCommunityClick,
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostDialog = true },
                containerColor = NavyBlue,
                contentColor = White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create post")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding)
        ) {
            // Share a tip card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { showCreatePostDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F4E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = NavyBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Share a safety tip...",
                        color = TextGray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Create post",
                        tint = NavyBlue
                    )
                }
            }

            // Tabs
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    FilterChip(
                        onClick = { selectedTab = tab },
                        label = { Text(tab) },
                        selected = selectedTab == tab,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyBlue,
                            selectedLabelColor = White
                        )
                    )
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
            // Error State
            else if (errorMessage != null && posts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CloudOff,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Couldn't load posts",
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { loadPosts() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            // Posts List
            else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts) { post ->
                        PostCard(
                            post = post,
                            onLikeClick = { toggleLike(post) },
                            onCommentClick = { /* Navigate to comments */ }
                        )
                    }
                    
                    // Empty state
                    if (posts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.Forum,
                                        contentDescription = null,
                                        tint = TextGray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "No posts yet",
                                        color = TextGray
                                    )
                                    Text(
                                        "Be the first to share!",
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
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
fun PostCard(
    post: PostResponse,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE4DE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.authorInitials,
                        fontWeight = FontWeight.Bold,
                        color = CoralOrange
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        post.authorName,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyBlue
                    )
                    Text(
                        formatTimeAgo(post.createdAt),
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                
                // Category badge
                if (post.category != "GENERAL") {
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (post.category) {
                                "SAFETY_ALERT" -> CoralOrange.copy(alpha = 0.1f)
                                "SAFETY_TIP" -> Color(0xFF22C55E).copy(alpha = 0.1f)
                                else -> NavyBlue.copy(alpha = 0.1f)
                            }
                        )
                    ) {
                        Text(
                            when (post.category) {
                                "SAFETY_ALERT" -> "⚠️ Alert"
                                "SAFETY_TIP" -> "💡 Tip"
                                "LOCAL_NEWS" -> "📰 News"
                                else -> post.category
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = when (post.category) {
                                "SAFETY_ALERT" -> CoralOrange
                                "SAFETY_TIP" -> Color(0xFF22C55E)
                                else -> NavyBlue
                            }
                        )
                    }
                }
                
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Filled.MoreHoriz,
                        contentDescription = "More",
                        tint = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                post.content,
                color = NavyBlue,
                lineHeight = 22.sp
            )
            
            // Location if available
            post.location?.address?.let { address ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        address,
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        if (post.isLikedByCurrentUser) Icons.Filled.ThumbUp else Icons.Filled.ThumbUpOffAlt,
                        contentDescription = "Like",
                        tint = if (post.isLikedByCurrentUser) NavyBlue else TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        post.likesCount.toString(),
                        color = if (post.isLikedByCurrentUser) NavyBlue else TextGray,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCommentClick() }
                ) {
                    Icon(
                        Icons.Filled.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        post.commentsCount.toString(),
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = TextGray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { /* Share */ }
                )
            }
        }
    }
}

// Helper function to format timestamp
fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> "${diff / 604_800_000}w ago"
    }
}

// Sample posts for fallback
fun getSamplePosts(): List<PostResponse> {
    val now = System.currentTimeMillis()
    return listOf(
        PostResponse(
            id = "1",
            authorId = "user1",
            authorName = "Jane Doe",
            authorInitials = "JD",
            content = "Reminder to everyone walking home late from the downtown area: please stick to well-lit streets. There have been a few reports of suspicious activity near Oak Street.",
            category = "SAFETY_ALERT",
            likesCount = 12,
            commentsCount = 5,
            isLikedByCurrentUser = false,
            createdAt = now - 7_200_000 // 2 hours ago
        ),
        PostResponse(
            id = "2",
            authorId = "user2",
            authorName = "John Smith",
            authorInitials = "JS",
            content = "Safety tip: Always share your live location with a trusted contact when traveling at night. GuardianX makes this super easy!",
            category = "SAFETY_TIP",
            likesCount = 24,
            commentsCount = 8,
            isLikedByCurrentUser = true,
            createdAt = now - 28_800_000 // 8 hours ago
        ),
        PostResponse(
            id = "3",
            authorId = "user3",
            authorName = "Emily Chen",
            authorInitials = "EC",
            content = "New police patrol station opened on Main Street. Great news for the community! 🎉",
            category = "LOCAL_NEWS",
            likesCount = 45,
            commentsCount = 12,
            isLikedByCurrentUser = false,
            createdAt = now - 86_400_000 // 1 day ago
        )
    )
}
