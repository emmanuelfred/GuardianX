package org.example.project.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.data.models.*
import org.example.project.data.repository.CommunityRepository
import org.example.project.data.repository.UserRepository
import org.example.project.plugins.getUserId
import org.example.project.utils.*

fun Route.communityRoutes() {
    val communityRepository = CommunityRepository()
    val userRepository = UserRepository()

    route("/community") {
        // Public routes - view posts without auth
        
        // Get all posts
        get("/posts") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            val category = call.request.queryParameters["category"]?.let { 
                try { PostCategory.valueOf(it.uppercase()) } catch (e: Exception) { null }
            }
            
            val posts = if (category != null) {
                communityRepository.findPostsByCategory(category, page, limit)
            } else {
                communityRepository.findAllPosts(page, limit)
            }
            
            val total = if (category != null) {
                communityRepository.countPostsByCategory(category)
            } else {
                communityRepository.countPosts()
            }
            
            // Get authors for all posts
            val postResponses = posts.mapNotNull { post ->
                val author = userRepository.findById(post.authorId)
                if (author != null) {
                    post.toResponse(author, "") // Empty userId for unauthenticated
                } else null
            }
            
            call.respond(
                HttpStatusCode.OK,
                PaginatedResponse(
                    success = true,
                    data = postResponses,
                    page = page,
                    limit = limit,
                    total = total,
                    totalPages = ((total + limit - 1) / limit).toInt()
                )
            )
        }

        // Get single post
        get("/posts/{id}") {
            val postId = call.parameters["id"]
                ?: throw BadRequestException("Post ID is required")
            
            val post = communityRepository.findPostById(postId)
                ?: throw NotFoundException("Post not found")
            
            val author = userRepository.findById(post.authorId)
                ?: throw NotFoundException("Author not found")
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    message = "Post found",
                    data = post.toResponse(author, "")
                )
            )
        }

        // Get comments for a post
        get("/posts/{id}/comments") {
            val postId = call.parameters["id"]
                ?: throw BadRequestException("Post ID is required")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
            
            val post = communityRepository.findPostById(postId)
                ?: throw NotFoundException("Post not found")
            
            val comments = communityRepository.findCommentsByPostId(postId, page, limit)
            val total = communityRepository.countCommentsByPostId(postId)
            
            val commentResponses = comments.mapNotNull { comment ->
                val author = userRepository.findById(comment.authorId)
                if (author != null) {
                    comment.toResponse(author, "")
                } else null
            }
            
            call.respond(
                HttpStatusCode.OK,
                PaginatedResponse(
                    success = true,
                    data = commentResponses,
                    page = page,
                    limit = limit,
                    total = total,
                    totalPages = ((total + limit - 1) / limit).toInt()
                )
            )
        }

        // Authenticated routes
        authenticate("auth-jwt") {
            // Get posts with like status for current user
            get("/posts/feed") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                val category = call.request.queryParameters["category"]?.let {
                    try { PostCategory.valueOf(it.uppercase()) } catch (e: Exception) { null }
                }
                
                val posts = if (category != null) {
                    communityRepository.findPostsByCategory(category, page, limit)
                } else {
                    communityRepository.findAllPosts(page, limit)
                }
                
                val total = if (category != null) {
                    communityRepository.countPostsByCategory(category)
                } else {
                    communityRepository.countPosts()
                }
                
                val postResponses = posts.mapNotNull { post ->
                    val author = userRepository.findById(post.authorId)
                    if (author != null) {
                        post.toResponse(author, userId)
                    } else null
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    PaginatedResponse(
                        success = true,
                        data = postResponses,
                        page = page,
                        limit = limit,
                        total = total,
                        totalPages = ((total + limit - 1) / limit).toInt()
                    )
                )
            }

            // Get current user's posts
            get("/posts/my") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
                
                val posts = communityRepository.findPostsByUserId(userId, page, limit)
                
                val user = userRepository.findById(userId)!!
                val postResponses = posts.map { it.toResponse(user, userId) }
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Your posts",
                        data = postResponses
                    )
                )
            }

            // Create new post
            post("/posts") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val request = call.receive<CreatePostRequest>()
                
                if (request.content.isBlank()) {
                    throw BadRequestException("Post content cannot be empty")
                }
                
                if (request.content.length > 2000) {
                    throw BadRequestException("Post content cannot exceed 2000 characters")
                }
                
                val location = if (request.latitude != null && request.longitude != null) {
                    Location(
                        latitude = request.latitude,
                        longitude = request.longitude,
                        address = request.address
                    )
                } else null
                
                val post = CommunityPost(
                    authorId = userId,
                    content = request.content,
                    category = request.category,
                    location = location,
                    imageUrls = request.imageUrls
                )
                
                communityRepository.createPost(post)
                
                val author = userRepository.findById(userId)!!
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Post created",
                        data = post.toResponse(author, userId)
                    )
                )
            }

            // Update post
            put("/posts/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["id"]
                    ?: throw BadRequestException("Post ID is required")
                val request = call.receive<UpdatePostRequest>()
                
                val post = communityRepository.findPostById(postId)
                    ?: throw NotFoundException("Post not found")
                
                if (post.authorId != userId) {
                    throw ForbiddenException("You can only edit your own posts")
                }
                
                val updatedPost = post.copy(
                    content = request.content ?: post.content,
                    category = request.category ?: post.category,
                    imageUrls = request.imageUrls ?: post.imageUrls
                )
                
                communityRepository.updatePost(updatedPost)
                
                val author = userRepository.findById(userId)!!
                
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        message = "Post updated",
                        data = updatedPost.toResponse(author, userId)
                    )
                )
            }

            // Delete post
            delete("/posts/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["id"]
                    ?: throw BadRequestException("Post ID is required")
                
                val post = communityRepository.findPostById(postId)
                    ?: throw NotFoundException("Post not found")
                
                if (post.authorId != userId) {
                    throw ForbiddenException("You can only delete your own posts")
                }
                
                communityRepository.deletePost(postId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Post deleted")
                )
            }

            // Like post
            post("/posts/{id}/like") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["id"]
                    ?: throw BadRequestException("Post ID is required")
                
                val post = communityRepository.findPostById(postId)
                    ?: throw NotFoundException("Post not found")
                
                communityRepository.likePost(postId, userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Post liked")
                )
            }

            // Unlike post
            delete("/posts/{id}/like") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["id"]
                    ?: throw BadRequestException("Post ID is required")
                
                val post = communityRepository.findPostById(postId)
                    ?: throw NotFoundException("Post not found")
                
                communityRepository.unlikePost(postId, userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Post unliked")
                )
            }

            // Add comment to post
            post("/posts/{id}/comments") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["id"]
                    ?: throw BadRequestException("Post ID is required")
                val request = call.receive<CreateCommentRequest>()
                
                val post = communityRepository.findPostById(postId)
                    ?: throw NotFoundException("Post not found")
                
                if (request.content.isBlank()) {
                    throw BadRequestException("Comment cannot be empty")
                }
                
                if (request.content.length > 500) {
                    throw BadRequestException("Comment cannot exceed 500 characters")
                }
                
                val comment = PostComment(
                    postId = postId,
                    authorId = userId,
                    content = request.content
                )
                
                communityRepository.createComment(comment)
                
                val author = userRepository.findById(userId)!!
                
                call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        success = true,
                        message = "Comment added",
                        data = comment.toResponse(author, userId)
                    )
                )
            }

            // Delete comment
            delete("/posts/{postId}/comments/{commentId}") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val postId = call.parameters["postId"]
                    ?: throw BadRequestException("Post ID is required")
                val commentId = call.parameters["commentId"]
                    ?: throw BadRequestException("Comment ID is required")
                
                val comment = communityRepository.findCommentById(commentId)
                    ?: throw NotFoundException("Comment not found")
                
                if (comment.authorId != userId) {
                    throw ForbiddenException("You can only delete your own comments")
                }
                
                communityRepository.deleteComment(commentId, postId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Comment deleted")
                )
            }

            // Like comment
            post("/comments/{id}/like") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val commentId = call.parameters["id"]
                    ?: throw BadRequestException("Comment ID is required")
                
                val comment = communityRepository.findCommentById(commentId)
                    ?: throw NotFoundException("Comment not found")
                
                communityRepository.likeComment(commentId, userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Comment liked")
                )
            }

            // Unlike comment
            delete("/comments/{id}/like") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.getUserId()
                val commentId = call.parameters["id"]
                    ?: throw BadRequestException("Comment ID is required")
                
                val comment = communityRepository.findCommentById(commentId)
                    ?: throw NotFoundException("Comment not found")
                
                communityRepository.unlikeComment(commentId, userId)
                
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(success = true, message = "Comment unliked")
                )
            }
        }
    }
}
