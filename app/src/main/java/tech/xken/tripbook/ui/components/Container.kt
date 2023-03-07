package tech.xken.tripbook.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String,
    errorText: () -> String? = { subtitle },
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable { onClick() },
        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = imageVector, contentDescription = null, modifier = Modifier
                    .padding(4.dp)
                    .weight(0.1f)
            )
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(0.9f, true)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(4.dp)
                )
                val color =
                    if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error
                Text(
                    text = errorText() ?: subtitle,
                    style = MaterialTheme.typography.caption.copy(color = color),
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }
}

@Composable
fun MainMenuItem(
    modifier: Modifier = Modifier,
    imageBitmap: Bitmap? = null,
    placeholder: ImageVector = Icons.Outlined.InsertPhoto,
    title: String,
    subtitle: String,
    errorText: () -> String? = { subtitle },
    onClick: () -> Unit,
) {

    Card(
        modifier = modifier.clickable { onClick() },
        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = placeholder, contentDescription = null, modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.8f, true)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(4.dp)
                )
                val color =
                    if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error
                Text(
                    text = errorText() ?: subtitle,
                    style = MaterialTheme.typography.caption.copy(color = color),
                    modifier = Modifier.padding(4.dp)
                )
            }

        }
    }
}