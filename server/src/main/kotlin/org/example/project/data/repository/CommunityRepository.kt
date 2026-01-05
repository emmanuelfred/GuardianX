package org.example.project.data.repository

import org.example.project.data.database.MongoDB
import org.example.project.data.models.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection

class CommunityRepository {
    private val postsCollection: CoroutineCollection<CommunityPost> = MongoDB.database.getCollection()
    private val commentsCollection: CoroutineCollection<PostComment> = MongoDB.database.getCollection()

    // Posts
    suspend fun createPost(post: CommunityPost): CommunityPost {
        postsCollection.insertOne(post)
        return post
    }

    suspend fun findPostById(id: String): CommunityPost? {
        return postsCollection.findOne(
            and(
                CommunityPost::id eq id,
                CommunityPost::isActive eq true
            )
        )
    }

    suspend fun findAllPosts(page: Int = 1, limit: Int = 20): List<CommunityPost> {
        return postsCollection
            .find(CommunityPost::isActive eq true)
            .sort(descending(CommunityPost::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findPostsByCategory(category: PostCategory, page: Int = 1, limit: Int = 20): List<CommunityPost> {
        return postsCollection
            .find(
                and(
                    CommunityPost::isActive eq true,
                    CommunityPost::category eq category
                )
            )
            .sort(descending(CommunityPost::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun findPostsByUserId(userId: String, page: Int = 1, limit: Int = 20): List<CommunityPost> {
        return postsCollection
            .find(
                and(
                    CommunityPost::authorId eq userId,
                    CommunityPost::isActive eq true
                )
            )
            .sort(descending(CommunityPost::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun updatePost(post: CommunityPost): Boolean {
        val result = postsCollection.updateOne(
            CommunityPost::id eq post.id,
            post.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun deletePost(postId: String): Boolean {
        val result = postsCollection.updateOne(
            CommunityPost::id eq postId,
            combine(
                setValue(CommunityPost::isActive, false),
                setValue(CommunityPost::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun likePost(postId: String, userId: String): Boolean {
        val post = findPostById(postId) ?: return false
        if (post.likes.contains(userId)) return true // Already liked
        
        val result = postsCollection.updateOne(
            CommunityPost::id eq postId,
            combine(
                setValue(CommunityPost::likes, post.likes + userId),
                setValue(CommunityPost::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun unlikePost(postId: String, userId: String): Boolean {
        val post = findPostById(postId) ?: return false
        if (!post.likes.contains(userId)) return true // Not liked
        
        val result = postsCollection.updateOne(
            CommunityPost::id eq postId,
            combine(
                setValue(CommunityPost::likes, post.likes - userId),
                setValue(CommunityPost::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun countPosts(): Long {
        return postsCollection.countDocuments(CommunityPost::isActive eq true)
    }

    suspend fun countPostsByCategory(category: PostCategory): Long {
        return postsCollection.countDocuments(
            and(
                CommunityPost::isActive eq true,
                CommunityPost::category eq category
            )
        )
    }

    // Comments
    suspend fun createComment(comment: PostComment): PostComment {
        commentsCollection.insertOne(comment)
        // Update comment count on post
        postsCollection.updateOne(
            CommunityPost::id eq comment.postId,
            inc(CommunityPost::commentsCount, 1)
        )
        return comment
    }

    suspend fun findCommentById(id: String): PostComment? {
        return commentsCollection.findOne(
            and(
                PostComment::id eq id,
                PostComment::isActive eq true
            )
        )
    }

    suspend fun findCommentsByPostId(postId: String, page: Int = 1, limit: Int = 20): List<PostComment> {
        return commentsCollection
            .find(
                and(
                    PostComment::postId eq postId,
                    PostComment::isActive eq true
                )
            )
            .sort(ascending(PostComment::createdAt))
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    suspend fun updateComment(comment: PostComment): Boolean {
        val result = commentsCollection.updateOne(
            PostComment::id eq comment.id,
            comment.copy(updatedAt = System.currentTimeMillis())
        )
        return result.modifiedCount > 0
    }

    suspend fun deleteComment(commentId: String, postId: String): Boolean {
        val result = commentsCollection.updateOne(
            PostComment::id eq commentId,
            combine(
                setValue(PostComment::isActive, false),
                setValue(PostComment::updatedAt, System.currentTimeMillis())
            )
        )
        if (result.modifiedCount > 0) {
            postsCollection.updateOne(
                CommunityPost::id eq postId,
                inc(CommunityPost::commentsCount, -1)
            )
        }
        return result.modifiedCount > 0
    }

    suspend fun likeComment(commentId: String, userId: String): Boolean {
        val comment = findCommentById(commentId) ?: return false
        if (comment.likes.contains(userId)) return true
        
        val result = commentsCollection.updateOne(
            PostComment::id eq commentId,
            combine(
                setValue(PostComment::likes, comment.likes + userId),
                setValue(PostComment::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun unlikeComment(commentId: String, userId: String): Boolean {
        val comment = findCommentById(commentId) ?: return false
        if (!comment.likes.contains(userId)) return true
        
        val result = commentsCollection.updateOne(
            PostComment::id eq commentId,
            combine(
                setValue(PostComment::likes, comment.likes - userId),
                setValue(PostComment::updatedAt, System.currentTimeMillis())
            )
        )
        return result.modifiedCount > 0
    }

    suspend fun countCommentsByPostId(postId: String): Long {
        return commentsCollection.countDocuments(
            and(
                PostComment::postId eq postId,
                PostComment::isActive eq true
            )
        )
    }
}
