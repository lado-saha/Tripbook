package tech.xken.tripbook.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
@Preview
fun AgencySearchResultItem() {
    ConstraintLayout(
        modifier = Modifier.padding(4.dp).fillMaxSize()
    ) {
        val (logoRef, bodyRef, nameref ) = createRefs()
    }
}