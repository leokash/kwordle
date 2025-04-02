
package com.nlkprojects.kwordle

import android.os.Bundle
import android.view.Window
import kotlinx.coroutines.*
import androidx.activity.ComponentActivity
import com.nlkprojects.kwordle.game.Engine
import androidx.navigation.compose.NavHost
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import com.nlkprojects.kwordle.game.words.WordProvider
import com.nlkprojects.kwordle.ui.theme.KWordleTheme
import com.nlkprojects.kwordle.ui.views.SettingsView
import com.nlkprojects.kwordle.ui.views.game.GameView
import androidx.navigation.compose.rememberNavController
import com.nlkprojects.kwordle.game.words.DefinitionFetcher
import com.nlkprojects.kwordle.ui.views.TitleView

enum class Routes {
    Help, Settings
}

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(1024, 1024)
        val prefs = Prefs(applicationContext)
        setContent {
            KWordleTheme {
                val engine = Engine(
                    prefs = prefs,
                    wordContext = WordProvider(resources, prefs, scope, DefinitionFetcher()),
                    coroutineScope = scope
                )

                val navController = rememberNavController()
                val titleButtonState = remember { mutableStateOf(true) }
                NavHost(navController = navController, startDestination = "Game") {
                    composable("Game") {
                        Scaffold(
                            topBar = {
                                TitleView(title = "Wordle", enabled = titleButtonState, onAction = {
                                    navController.navigate(it.name)
                                })
                            }
                        ) { padding ->
                            Box (modifier = Modifier.padding(paddingValues = padding)) {
                                GameView(engine)
                            }
                        }
                    }
                    composable("Settings") {
                        SettingsView(titleButtonState, navController)
                    }
                }
            }
        }
    }
}
