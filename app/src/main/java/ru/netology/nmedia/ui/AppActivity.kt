package ru.netology.nmedia.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog
import com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable
import com.google.android.gms.common.api.GoogleApi
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.R
import ru.netology.nmedia.api.*
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.service.FCMService
import ru.netology.nmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.token
import java.security.PrivateKey
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    @Inject
    lateinit var auth: AppAuth


    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_fragmentEditPost,
                    Bundle().apply {
                        textArg = text
                    }
                )

            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,

                    Bundle().apply {
                        textArg = text
                    }
                )


            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_fragmentEnter,

                    Bundle().apply {
                        textArg = text
                    }
                )


            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_fragmentRegistration,

                    Bundle().apply {
                        textArg = text
                    }
                )

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_fragmentLargePhoto,
                Bundle().apply {
                    textArg = text
                }
            )


        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        checkGoogleApiAvailability()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.let {

            if (findNavController(R.id.nav_host_fragment).currentDestination?.getId() == R.id.feedFragment) {

                it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                it.setGroupVisible(R.id.authenticated, viewModel.authenticated)

            } else {

                it.setGroupVisible(
                    R.id.unauthenticated,
                    findNavController(R.id.nav_host_fragment).currentDestination?.getId() == R.id.feedFragment
                            && !viewModel.authenticated
                )

                it.setGroupVisible(
                    R.id.authenticated,
                    findNavController(R.id.nav_host_fragment).currentDestination?.getId() == R.id.feedFragment
                            && viewModel.authenticated
                )

                it.setGroupVisible(R.id.authenticated, !viewModel.authenticated)

            }
            return true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                invalidateOptionsMenu()
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_feedFragment_to_fragmentEnter)

                true
            }
            R.id.signup -> {
                invalidateOptionsMenu()
                findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_feedFragment_to_fragmentRegistration)

                true
            }
            R.id.signout -> {
                invalidateOptionsMenu()
                findNavController(R.id.nav_host_fragment).navigateUp()
                auth.removeAuth()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }
}






