package tech.xken.tripbook.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout


@Composable
fun PhotoPicker(
    photoBitmap: Bitmap?,
    placeholder: ImageVector,
    onBrowseGalleryClick: () -> Unit,
    onLaunchCameraClick: () -> Unit = {},
    onDeletePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,

) {
    val fabSize = 32.dp
    val fabShape = RoundedCornerShape(20f)
    ConstraintLayout(
        modifier = modifier
    ) {
        val (photoRef, cameraBtnRef, galleryBtnRef, clearBtnRef) = createRefs()
        Crossfade(
            targetState = photoBitmap,
            modifier = Modifier
                .constrainAs(photoRef) { centerTo(parent) }
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            if (it != null) Image(bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize())
            else Image(imageVector = placeholder,
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxSize())
        }
        //Delete Button
        Crossfade(targetState = photoBitmap, modifier = Modifier
            .constrainAs(clearBtnRef) {
                centerVerticallyTo(parent, 0f)
                start.linkTo(parent.start)
                end.linkTo(photoRef.start)
            }
            .padding(2.dp)
            .size(fabSize)) {
            if (it != null) FloatingActionButton(onClick = {onDeletePhotoClick() },
                shape = fabShape) {
                Icon(imageVector = Icons.Outlined.Delete,
                    contentDescription = null)
            }
        }

        //Change Button
        FloatingActionButton(onClick = {onBrowseGalleryClick() }, shape = fabShape,
            modifier = Modifier
                .constrainAs(galleryBtnRef) {
                    centerVerticallyTo(parent, 1f)
                    start.linkTo(photoRef.end)
                    end.linkTo(parent.end)
                }
                .padding(2.dp)
                .size(fabSize)) {
            Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
        }

        //Camera Button
        FloatingActionButton(onClick = { onLaunchCameraClick() }, shape = fabShape,
            modifier = Modifier
                .constrainAs(cameraBtnRef) {
                    centerVerticallyTo(parent, 0f)
                    start.linkTo(photoRef.end)
                    end.linkTo(parent.end)
                }

                .padding(2.dp)
                .size(fabSize)) {
            Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = null)
        }
    }
}