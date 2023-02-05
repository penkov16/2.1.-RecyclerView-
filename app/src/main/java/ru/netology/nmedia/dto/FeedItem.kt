package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

sealed class FeedItem {
    abstract val id: Long
}

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
    val timing: Timing
) : FeedItem()

data class Timing(
    override val id: Long,
    val timing: String

) : FeedItem()


data class Post(
    override val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    val likes: Int = 0,
    val newer: Long,
    val authorId: Long,
    var ownedByMe: Boolean = false,
    val attachment: Attachment? = null,

    ) : FeedItem()

data class Attachment(
    val url: String,
    val type: AttachmentType,
)


