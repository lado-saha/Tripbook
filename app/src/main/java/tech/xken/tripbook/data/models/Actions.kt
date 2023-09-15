package tech.xken.tripbook.data.models

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.xken.tripbook.domain.titleCase

data class MainAction(
    val nameStr: Int,
    val icon: ImageVector,
) {

}

@Composable
fun ActionItem(
    action: MainAction,
    onClick: (MainAction) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isBold: Boolean = false
) {
//    val backgroundColor = Brush.linearGradient(
//        listOf(
//            MaterialTheme.colors.primary.copy(alpha = 0.5f),
//            MaterialTheme.colors.error.copy(alpha = 0.5f),
//            ColorWarn.copy(alpha = 0.5f)
//        )
//    )
    val backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.5f)

    Row(
        horizontalArrangement = Arrangement.Start,
        modifier =
        if (isSelected) modifier
            .fillMaxWidth(0.95f)
            .background(
                backgroundColor,
                shape = RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
            )
            .clickable { onClick(action) }
            .padding(vertical = 12.dp)
        else modifier
            .clickable { onClick(action) }
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp, end = 32.dp),
        )
        Text(
            text = stringResource(id = action.nameStr).titleCase,
            fontWeight = if (isBold) FontWeight.Bold else null
        )
    }
}

@Composable
fun ActionSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Divider(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .width(64.dp),
                thickness = 4.dp
            )
            content()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}