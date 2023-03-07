package tech.xken.tripbook.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UnivResultItem(
    modifier: Modifier = Modifier,
    name: String = "",
    isFullySelected: Boolean? = null,
) {
    Card() {
        Row() {
            Text(text = name)


        }
    }
}

@Preview
@Composable
fun UnivResultPrev(){
    UnivResultItem()
}