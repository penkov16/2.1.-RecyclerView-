package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEnterBinding
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class FragmentEnter : Fragment() {

    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private var fragmentBinding: FragmentEnter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEnterBinding.inflate(
            inflater,
            container,
            false
        )


        authViewModel.data.observe(viewLifecycleOwner) { authState ->
            if (authState.id != 0L) {

                findNavController().navigateUp()
            }
        }

        binding.enter.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())

            authViewModel.loginUser(
                login = binding.login.text.toString(),
                pass = binding.pass.text.toString()
            )

        }

        authViewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                binding.root,
                "${getString(R.string.error_loading)}: ${error.message}",

                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.retry_loading) {
                    when (error.action) {
                        ActionType.LoginUser ->
                            authViewModel.loginUser(
                                login = binding.login.toString(),
                                pass = binding.pass.toString()
                            )

                    }
                }
                show()
            }
        }

        return binding.root
    }

}