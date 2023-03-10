package ru.netology.nmedia.adapter

import Wallsevice
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardTimingBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Timing
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject


class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeTiming = 0
    private val typeAd = 1
    private val typePost = 2

    interface OnInteractionListener {
        fun onLike(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onShare(post: Post) {}
        fun pushPhoto(post: Post) {}
        fun onAdClick(ad: Ad) {}
        fun onTimingClick(timing: Timing) {}
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Ad -> typeAd
            is Timing -> typeTiming
            is Post -> typePost

            null -> throw IllegalArgumentException("unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )
            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            typeTiming -> TimingViewHolder(
                CardTimingBinding.inflate(layoutInflater, parent, false),
                onInteractionListener

            )
            else -> throw IllegalArgumentException("unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            when (it) {
                is Post -> (holder as? PostViewHolder)?.bind(it)
                is Ad -> (holder as? AdViewHolder)?.bind(it)

                is Timing -> (holder as? TimingViewHolder)?.bind(it)
            }
        }
    }


    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {


        val service = Wallsevice()
        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published.toString()
                content.text = post.content
                avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
                like.isChecked = post.likedByMe
                like.text = "${service.zeroingOutLikes(post.likes.toLong())}"

                photo.setImageURI(Uri.parse("${BuildConfig.BASE_URL}/attachment/??????_????????????????.jpg"))

                photo.isVisible = post.attachment != null
                post.attachment?.let {
                    Log.d("MyLog", "${BuildConfig.BASE_URL}/media/${it.url}")
                    Glide.with(photo)
                        .load("${BuildConfig.BASE_URL}/media/${it.url}")
                        .timeout(10_000)
                        .into(photo)
                }


                menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

                menu.setOnClickListener {

                    PopupMenu(it.context, it).apply {

                        inflate(R.menu.options_post)
                        // TODO: if we don't have other options, just remove dots

                        menu.setGroupVisible(R.id.owned, post.ownedByMe)

                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }

                                else -> false
                            }
                        }
                    }.show()
                }

                like.setOnClickListener {
                    onInteractionListener.onLike(post)
                }

                share.setOnClickListener {
                    onInteractionListener.onShare(post)
                }

                photoContainer.setOnClickListener {
                    onInteractionListener.pushPhoto(post)
                }

            }
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {


        val service = Wallsevice()

        fun bind(ad: Ad) {
            binding.apply {
                image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
                image.setOnClickListener {
                    onInteractionListener.onAdClick(ad)
                }
                timing.text = "${service.agoToText}"
            }
        }
    }


    class TimingViewHolder(
        private val binding: CardTimingBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(timings: Timing) {
            binding.apply {
                val service = Wallsevice()

                timing.text = "${service.agoToText}"

                timing.setOnClickListener {
                    onInteractionListener.onTimingClick(timings)
                }
            }
        }
    }


    class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}







