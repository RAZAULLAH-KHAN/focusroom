package com.focusroom.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.focusroom.app.core.theme.BackgroundDark
import com.focusroom.app.core.theme.FocusRoomTheme
import com.focusroom.app.service.ShameForegroundService
import com.focusroom.app.ui.dashboard.DashboardScreen
import com.focusroom.app.ui.dashboard.DashboardViewModel
import com.focusroom.app.ui.room.FocusRoomScreen
import com.focusroom.app.ui.room.FocusRoomViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var shameService: ShameForegroundService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as? ShameForegroundService.ShameBinder
            shameService = serviceBinder?.getService()
            isBound = true
            Log.d("MainActivity", "Bound to ShameForegroundService successfully.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shameService = null
            isBound = false
            Log.d("MainActivity", "Disconnected from ShameForegroundService.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start and bind the Shame Engine service immediately
        startShameService()

        setContent {
            FocusRoomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            val dashboardViewModel = hiltViewModel<DashboardViewModel>()
                            DashboardScreen(
                                viewModel = dashboardViewModel,
                                onRoomClick = { roomId ->
                                    navController.navigate("room/$roomId")
                                }
                            )
                        }
                        
                        composable(
                            route = "room/{roomId}",
                            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val roomId = backStackEntry.arguments?.getString("roomId") ?: "room_lofi"
                            val roomViewModel = hiltViewModel<FocusRoomViewModel>()
                            FocusRoomScreen(
                                viewModel = roomViewModel,
                                roomId = roomId,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startShameService() {
        try {
            val intent = Intent(this, ShameForegroundService::class.java)
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start or bind ShameForegroundService", e)
        }
    }

    override fun onStart() {
        super.onStart()
        // App returned to foreground: restore state
        if (isBound) {
            shameService?.onAppRestored()
        }
    }

    override fun onStop() {
        super.onStop()
        // App minimized to background: trigger shame state!
        if (isBound) {
            shameService?.onAppMinimized()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}
