package tech.xken.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Container() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(0.9f)
    ) {
        Box() {
            Card() {
                Row(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.FullscreenExit, contentDescription = "")
                    Text(
                        modifier = Modifier.weight(0.8f, fill = true),
                        text = "West Region", style = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                }
            }
        }
    }
}
@Preview
@Composable
fun ContainerPrev(){
    Container()
}