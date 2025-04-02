
package com.nlkprojects.kwordle.ui.views.keyboard

import android.annotation.SuppressLint
import com.nlkprojects.kwordle.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.nlkprojects.kwordle.Event.Keyboard
import com.nlkprojects.kwordle.KeyboardEventBroadcaster
import com.nlkprojects.kwordle.Event.Keyboard as KeyboardEvent
import com.nlkprojects.kwordle.Event.Keyboard.Validated.State as KeyState

private class KeyUpdateHandler: KeyUpdateBroadcaster {
    private val observers = mutableListOf<(KeyUpdate) -> Unit>()
    fun handle(update: KeyUpdate) {
        observers.onEach { it(update) }
    }
    override fun register(observer: (KeyUpdate) -> Unit) {
        observers += observer
    }
}
private fun KeyboardEvent.Validated.mapToUpdate(): KeyUpdate {
    return KeyUpdate.Update(char.titlecaseChar(), state == KeyState.Success)
}

private val width = 28.dp
private val height = 48.dp
private val spacing = 4.dp

@Composable
fun KeyboardView(
    enabled: MutableState<Boolean>,
    broadcaster: KeyboardEventBroadcaster,
    onTappedAction: (KeyType) -> Unit
) {
    val handler = KeyUpdateHandler()
    broadcaster.register { event ->
        when(event) {
            is KeyboardEvent.Reset -> handler.handle(KeyUpdate.Reset)
            is KeyboardEvent.Validated -> handler.handle(event.mapToUpdate())
        }
    }
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        KeyboardRow(
            listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'),
            enabled,
            Modifier,
            handler,
            onTappedAction
        ) {
            KeyCard(
                KeyType.Delete,
                DpSize(width * 2, height),
                enabled,
                fontFamily = FontFamily(Font(R.font.fa_solid_900)),
                broadcaster = handler,
                onTapCallback = onTappedAction
            )
        }

        KeyboardRow(
            listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'),
            enabled,
            Modifier.absolutePadding(top = 8.dp),
            handler, onTappedAction
        ) {
            EnterCard(
                DpSize(width * 2, height),
                enabled,
                Modifier.padding(0.dp),
                onTappedAction
            )
        }

        KeyboardRow(
            listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M'),
            enabled,
            Modifier.absolutePadding(top = 8.dp),
            handler, onTappedAction
        )
    }
}

@Composable
private fun KeyboardRow(keys: List<Char>,
                        enabled: MutableState<Boolean>,
                        modifier: Modifier,
                        broadcaster: KeyUpdateHandler,
                        onTappedAction: (KeyType) -> Unit,
                        content: @Composable RowScope.() -> Unit = { }) {
    Row (
        modifier.fillMaxWidth(),
        Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
    ) {
        keys.forEach { char ->
            KeyCard(
                KeyType.Value(char),
                DpSize(width, height),
                enabled,
                fontFamily = null,
                broadcaster = broadcaster,
                onTapCallback = onTappedAction)
        }
        content()
    }
}

@Preview
@Composable
@SuppressLint("UnrememberedMutableState")
fun KeyboardViewPreview() {
    KeyboardView(
        mutableStateOf(true),
        object: KeyboardEventBroadcaster {
            override fun register(observer: (Keyboard) -> Unit) { }
        }
    ) {

    }
}
