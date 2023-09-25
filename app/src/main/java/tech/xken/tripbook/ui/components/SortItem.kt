package tech.xken.tripbook.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.SortField
import tech.xken.tripbook.domain.titleCase

@Composable
fun SortItem(
    modifier: Modifier = Modifier,
    field: SortField,
    isSelected: Boolean,
    onAscendingClick: (field: SortField) -> Unit,
    onDescendingClick: (field: SortField) -> Unit,
) {
    Card(
        modifier = modifier,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colors.primary) else null,
//        elevation = if (isSelected) 8.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = field.nameRes).titleCase, modifier = Modifier.padding(2.dp).weight(0.8f))

            IconButton(onClick = { onDescendingClick(field) }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = stringResource(
                        id = R.string.desc_descending_order,
                    ),
                    tint = if (field.ascending == false) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                        alpha = LocalContentAlpha.current
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp).weight(0.1f)
                )
            }

            IconButton(onClick = {
                onAscendingClick(field)
            }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = stringResource(
                        id = R.string.desc_ascending_order,
                    ),
                    tint = if (field.ascending == true) MaterialTheme.colors.primary else LocalContentColor.current.copy(
                        alpha = LocalContentAlpha.current
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp).weight(0.1f)
                )
            }

        }
    }
}