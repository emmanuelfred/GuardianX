package org.example.project.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
enum class PostCategory {
    SAFETY_ALERT,
    SAFETY_TIP,
    LOCAL_NEWS,
    GENERAL
}

@Serializable
data class CommunityPost(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val authorId: String,
    val content: String,
    val category: PostCategory = PostCategory.GENERAL,
    val location: Location? = null, // Optional location tagging
    val imageUrls: List<String> = emptyList(),
    val likes: List<String> = emptyList(), // User IDs who liked
    val commentsCount: Int = 0,
    val isActive: Boolean = true,
    val isReported: Boolean = false,
    val reportCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class PostComment(
    @SerialName("_id")
    val id: String = ObjectId().toString(),
    val postId: String,
    val authorId: String,
    val content: String,
    val likes: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CreatePostRequest(
    val content: String,
    val category: PostCategory = PostCategory.GENERAL,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val imageUrls: List<String> = emptyList()
)

@Serializable
data class UpdatePostRequest(
    val content: String? = null,
    val category: PostCategory? = null,
    val imageUrls: List<String>? = null
)

@Serializable
data class CreateCommentRequest(
    val content: String
)

@Serializable
data class PostResponse(
    val id: String,
    val author: PostAuthor,
    val content: String,
    val category: PostCategory,
    val location: Location?,
    val imageUrls: List<String>,
    val likesCount: Int,
    val commentsCount: Int,
    val isLikedByCurrentUser: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class PostAuthor(
    val id: String,
    val fullName: String,
    val profileImageUrl: String?
)

@Serializable
data class CommentResponse(
    val id: String,
    val postId: String,
    val author: PostAuthor,
    val content: String,
    val likesCount: Int,
    val isLikedByCurrentUser: Boolean,
    val createdAt: Long
)

fun CommunityPost.toResponse(author: User, currentUserId: String) = PostResponse(
    id = id,
    author = PostAuthor(
        id = author.id,
        fullName = author.fullName,
        profileImageUrl = author.profileImageUrl
    ),
    content = content,
    category = category,
    location = location,
    imageUrls = imageUrls,
    likesCount = likes.size,
    commentsCount = commentsCount,
    isLikedByCurrentUser = likes.contains(currentUserId),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PostComment.toResponse(author: User, currentUserId: String) = CommentResponse(
    id = id,
    postId = postId,
    author = PostAuthor(
        id = author.id,
        fullName = author.fullName,
        profileImageUrl = author.profileImageUrl
    ),
    content = content,
    likesCount = likes.size,
    isLikedByCurrentUser = likes.contains(currentUserId),
    createdAt = createdAt
)
