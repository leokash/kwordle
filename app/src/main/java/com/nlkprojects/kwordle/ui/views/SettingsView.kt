package com.nlkprojects.kwordle.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.guru.fontawesomecomposelib.FaIcons
import com.nlkprojects.kwordle.Routes

@Composable
fun SettingsView(enabled: MutableState<Boolean>, navigation: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Box(
            Modifier
                .height(64.dp)
                .shadow(elevation = 1.dp, spotColor = Color.Black)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TitleText("Settings", modifier = Modifier.align(Alignment.Center))
            TitleButton(FaIcons.Hamburger, enabled, modifier = Modifier.align(Alignment.CenterEnd)) {
                navigation.navigate(Routes.Help.name)
            }
            TitleButton(FaIcons.ChevronLeft, enabled, modifier = Modifier.align(Alignment.CenterStart)) {
                navigation.popBackStack()
            }
        }
    }
}
