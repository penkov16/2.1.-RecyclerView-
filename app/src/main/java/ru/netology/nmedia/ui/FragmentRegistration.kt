package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.model.ActionType
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FragmentRegistration : Fragment() {


    private val authViewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )


    private var fragmentBinding: FragmentRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ru.netology.nmedia.databinding.FragmentRegistrationBinding.inflate(
            inflater,
            container,
            false
        )


        authViewModel.data.observe(viewLifecycleOwner) { authState ->
            if (authState.id != 0L) {

                findNavController().navigateUp()
            }
        }


        binding.register.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())

            val login = binding.login.text.toString()
            val name = binding.name.text.toString()
            val pass = binding.pass.text.toString()
            val pass2 = binding.pass2.text.toString()

            if (pass == pass2)
                authViewModel.registerUser(login, name, pass)
            else Snackbar.make(
                binding.root,
                "${getString(R.string.password_does_not_match)}",
                Snackbar.LENGTH_INDEFINITE
            )
                .show()

        }
        authViewModel.error.observe(viewLifecycleOwner) { error ->
            Snackbar.make(
                binding.root,
                "${getString(R.string.error_loading)}: ${error.message}",

                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.retry_loading) {
                    when (error.action) {
                        ActionType.RegisterUser -> authViewModel.registerUser(

                            login = binding.login.toString(),
                            name = binding.name.toString(),
                            pass = binding.pass.toString(),
                        )

                    }
                }
                show()
            }
        }


        return binding.root
    }
}