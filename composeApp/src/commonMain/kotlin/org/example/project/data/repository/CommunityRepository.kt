package org.example.project.data.repository

import io.ktor.client.request.*
import org.example.project.data.api.ApiClient
import org.example.project.data.api.ApiResult
import org.example.project.data.api.safeApiCall
import org.example.project.data.models.*

/**
 * Repository for community posts and comments API calls
 */
class CommunityRepository {
    
    private val client = ApiClient.httpClient
    
    // ==================== POSTS ====================
    
    /**
     * Get all posts (public, no auth required)
     */
    suspend fun getPosts(page: Int = 1, limit: Int = 20, category: String? = null): ApiResult<PaginatedResponse<PostResponse>> = safeApiCall {
        client.get("/community/posts") {
            parameter("page", page)
            parameter("limit", limit)
            category?.let { parameter("category", it) }
        }
    }
    
    /**
     * Get posts feed with like status (requires auth)
     */
    suspend fun getFeed(page: Int = 1, limit: Int = 20, category: String? = null): ApiResult<PaginatedResponse<PostResponse>> = safeApiCall {
        client.get("/community/posts/feed") {
            parameter("page", page)
            parameter("limit", limit)
            category?.let { parameter("category", it) }
        }
    }
    
    /**
     * Get current user's posts
     */
    suspend fun getMyPosts(page: Int = 1, limit: Int = 20): ApiResult<ApiResponse<List<PostResponse>>> = safeApiCall {
        client.get("/community/posts/my") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Get single post by ID
     */
    suspend fun getPost(id: String): ApiResult<ApiResponse<PostResponse>> = safeApiCall {
        client.get("/community/posts/$id")
    }
    
    /**
     * Create new post
     */
    suspend fun createPost(
        content: String,
        category: String = "GENERAL",
        latitude: Double? = null,
        longitude: Double? = null,
        address: String? = null,
        imageUrls: List<String> = emptyList()
    ): ApiResult<ApiResponse<PostResponse>> = safeApiCall {
        client.post("/community/posts") {
            setBody(CreatePostRequest(content, category, latitude, longitude, address, imageUrls))
        }
    }
    
    /**
     * Update post
     */
    suspend fun updatePost(
        postId: String,
        content: String? = null,
        category: String? = null,
        imageUrls: List<String>? = null
    ): ApiResult<ApiResponse<PostResponse>> = safeApiCall {
        client.put("/community/posts/$postId") {
            setBody(mapOf(
                "content" to content,
                "category" to category,
                "imageUrls" to imageUrls
            ).filterValues { it != null })
        }
    }
    
    /**
     * Delete post
     */
    suspend fun deletePost(postId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/community/posts/$postId")
    }
    
    /**
     * Like a post
     */
    suspend fun likePost(postId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/community/posts/$postId/like")
    }
    
    /**
     * Unlike a post
     */
    suspend fun unlikePost(postId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/community/posts/$postId/like")
    }
    
    // ==================== COMMENTS ====================
    
    /**
     * Get comments for a post
     */
    suspend fun getComments(postId: String, page: Int = 1, limit: Int = 20): ApiResult<PaginatedResponse<CommentResponse>> = safeApiCall {
        client.get("/community/posts/$postId/comments") {
            parameter("page", page)
            parameter("limit", limit)
        }
    }
    
    /**
     * Add comment to post
     */
    suspend fun addComment(postId: String, content: String): ApiResult<ApiResponse<CommentResponse>> = safeApiCall {
        client.post("/community/posts/$postId/comments") {
            setBody(CreateCommentRequest(content))
        }
    }
    
    /**
     * Delete comment
     */
    suspend fun deleteComment(postId: String, commentId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/community/posts/$postId/comments/$commentId")
    }
    
    /**
     * Like a comment
     */
    suspend fun likeComment(commentId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.post("/community/comments/$commentId/like")
    }
    
    /**
     * Unlike a comment
     */
    suspend fun unlikeComment(commentId: String): ApiResult<SimpleResponse> = safeApiCall {
        client.delete("/community/comments/$commentId/like")
    }
}
