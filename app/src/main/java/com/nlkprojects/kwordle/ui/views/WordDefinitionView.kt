package com.nlkprojects.kwordle.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nlkprojects.kwordle.ui.theme.Wedgewood
import com.nlkprojects.kwordle.ui.theme.White
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.nlkprojects.kwordle.game.words.DefinedWord
import com.nlkprojects.kwordle.ui.theme.DarkerGrey

@Composable
fun WordDefinitionView(
    wordDef: DefinedWord,
    modifier: Modifier = Modifier,
    primaryColour: Color,
    secondaryColour: Color
) {
    val spacing = 8.dp
    val fontSize = 16.sp

    Column(
        modifier
            .padding(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Row(
            Modifier.height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.Start)
        ) {
            Text(text = wordDef.value, color = primaryColour, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = wordDef.type.name, color = secondaryColour, fontSize = fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = 8.dp))
        }

        wordDef.phonetic?.let { phonetic ->
            val height = 32.dp
            Row(
                Modifier
                    .height(height),
                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.Start)
            ) {
                Text(text = phonetic.syllables, color = primaryColour, fontSize = fontSize)
                phonetic.ipa?.let { ipa ->
                    Box(
                        Modifier
                            .offset(y = -(3.dp))
                            .fillMaxHeight(.9f)
                            .border(2.dp, secondaryColour, RoundedCornerShape(percent = 45)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = ipa, modifier = Modifier.padding(horizontal = 8.dp), color = secondaryColour, fontSize = fontSize) //, onTextLayout = { width.value = (it.size.width * 1.8).dp })
                    }
                }
            }
        }

        val defHeight = remember { mutableStateOf(48.dp) }
        Row(
            Modifier
                .height(defHeight.value)
        ) {
            Text(
                text = wordDef.definition.capitalize(Locale.current),
                color = primaryColour,
                fontSize = fontSize,
                onTextLayout = { defHeight.value = (it.size.height.toFloat() * 2f).dp }
            )
        }
    }
}

@Preview
@Composable
fun WordDefinitionViewPreview() {
    WordDefinitionView(
        DefinedWord(
            DefinedWord.Type.Noun,
            "bilby",
            DefinedWord.Phonetic("ˈbil-bē", "bil·by"),
            "either of two burrowing nocturnal bandicoots (Macrotis lagotis and M. leucura) having a long tapered muzzle and large pointed ears"
        ),
        Modifier
            .fillMaxWidth()
            .background(DarkerGrey)
            .height(IntrinsicSize.Min),
        White,
        Wedgewood
    )
}
