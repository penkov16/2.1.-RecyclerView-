package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLargePhotoBinding
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FragmentLargePhoto : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLargePhotoBinding.inflate(
            inflater, container, false
        )

        val id = arguments?.getLong("id") ?: -1L

        lifecycleScope.launchWhenCreated {
            viewModel.getById(id).collectLatest { post ->
                post ?: run {
                    findNavController().navigateUp()
                    return@collectLatest
                }

                binding.apply {
                    like.isChecked = post.likedByMe
                    like.text = post.likes.toString()
                    post.attachment?.let {
                        val url = "${BuildConfig.BASE_URL}/media/${it.url}"
                        Log.d("MyLog", url)
                        Glide.with(photo)
                            .load(url)
                            .timeout(10_000)
                            .into(photo)
                    }
                    like.setOnClickListener {
                        if (post.likedByMe) {
                            viewModel.unlikeById(post.id)
                        } else {
                            viewModel.likeById(post.id)
                        }
                    }
                    share.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.content)
                            type = "text/plain"
                        }
                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                binding.root,
                "${getString(R.string.error_loading)}: ${error.message}",
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.retry_loading) {
                    when (error.action) {
                        ActionType.Like -> viewModel.likeById(id)
                        ActionType.unlikeById -> viewModel.unlikeById(id)
                    }
                }
                show()
            }
        }
        return binding.root
    }
}