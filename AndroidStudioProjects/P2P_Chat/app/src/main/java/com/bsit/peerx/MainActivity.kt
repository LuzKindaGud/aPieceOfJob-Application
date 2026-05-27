package com.bsit.peerx

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bsit.peerx.ui.chat.ChatRoute
import com.bsit.peerx.ui.chat.ChatViewModel
import com.bsit.peerx.ui.discovery.DiscoveryRoute
import com.bsit.peerx.ui.discovery.DiscoveryViewModel
import com.bsit.peerx.ui.theme.PeerXTheme
import dagger.hilt.android.AndroidEntryPoint
import android.net.Uri

import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bsit.peerx.lock.AppLockManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var appLockManager: AppLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isLocked by appLockManager.isLocked.collectAsStateWithLifecycle()
            PeerXTheme {
//                if (isLocked) {
//                    LockScreen(onUnlocked = { appLockManager.unlock() })
//                } else {
//                    AppNavigation()
//                }
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Handle permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // We can handle results here if needed, but the UI will update
        // when permissions are granted and discovery is retried.
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // DisposableEffect bÃ¢y giá» sáº½ an toÃ n vÃ¬ nÃ³ gá»i cÃ¡c hÃ m trong ViewModel
    // Ä‘Ã£ Ä‘Æ°á»£c khá»Ÿi táº¡o Ä‘Ãºng cÃ¡ch.
    DisposableEffect(lifecycleOwner, mainViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mainViewModel.registerReceiver()
                Lifecycle.Event.ON_PAUSE -> mainViewModel.unregisterReceiver()
                else -> {} // KhÃ´ng lÃ m gÃ¬ á»Ÿ cÃ¡c event khÃ¡c
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "discovery") {
        // MÃ n hÃ¬nh Discovery
        composable("discovery") {
            val discoveryViewModel: DiscoveryViewModel = hiltViewModel()
            DiscoveryRoute(
                viewModel = discoveryViewModel,
                onOpenChat = { peerAddress ->
                    navController.navigate("chat/${Uri.encode(peerAddress)}")
                }
            )
        }

        // MÃ n hÃ¬nh Chat
        composable(
            route = "chat/{peerAddress}",
            arguments = listOf(navArgument("peerAddress") { type = NavType.StringType })
        ) {
            val chatViewModel: ChatViewModel = hiltViewModel(it)
            ChatRoute(
                viewModel = chatViewModel,
                onBack = { navController.popBackStack() },
                peerAddress = it.arguments?.getString("peerAddress")!!
            )
        }
    }
}




