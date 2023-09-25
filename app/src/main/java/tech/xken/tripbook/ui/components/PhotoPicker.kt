package tech.xken.tripbook.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import tech.xken.tripbook.R


@Composable
fun PhotoPicker(
    photoBitmap: Bitmap?,
    placeholder: ImageVector,
    onBrowseGalleryClick: () -> Unit,
    onLaunchCameraClick: () -> Unit = {},
    onDeletePhotoClick: () -> Unit,
    onReloadPhotoClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFailure: Boolean = false,
    failurePlaceholder: ImageVector = Icons.Default.BrokenImage
) {
    val fabSize = 32.dp
    val fabShape = RoundedCornerShape(20f)
    ConstraintLayout(
        modifier = modifier
    ) {
        val (photoRef, cameraBtnRef, galleryBtnRef, clearBtnRef, loadingRef, reloadRef) = createRefs()

        Crossfade(
            targetState = photoBitmap,
            modifier = Modifier
                .constrainAs(photoRef) { centerTo(parent) }
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            if (isFailure)
                Image(
                    imageVector = failurePlaceholder,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxSize()
                )

            if (it != null) Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            else Image(
                imageVector = placeholder,
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalContentColor.current.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isLoading)
            CircularProgressIndicator(modifier = Modifier
                .padding(2.dp)
                .constrainAs(loadingRef) {
                    centerVerticallyTo(photoRef)
                    centerHorizontallyTo(photoRef)
                }
            )

//Delete
        AnimatedVisibility(
            visible = photoBitmap != null && !isLoading && !isFailure,
            modifier = Modifier
                .constrainAs(clearBtnRef) {
                    centerVerticallyTo(parent, 0f)
                    start.linkTo(parent.start)
                    end.linkTo(photoRef.start)
                }
                .padding(2.dp)
                .size(fabSize)) {
            FloatingActionButton(
                onClick = { onDeletePhotoClick() },
                shape = fabShape
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.desc_del_photo)
                )
            }
        }

//Change Button
        AnimatedVisibility(
            visible = !isLoading && !isFailure,
            modifier = Modifier
                .constrainAs(galleryBtnRef) {
                    centerVerticallyTo(parent, 1f)
                    start.linkTo(photoRef.end)
                    end.linkTo(parent.end)
                }
                .padding(2.dp)
                .size(fabSize)) {
            FloatingActionButton(
                onClick = { onBrowseGalleryClick() },
                shape = fabShape
            ) {
                Icon(imageVector = Icons.Outlined.FileUpload, contentDescription = stringResource(R.string.desc_pick_photo_gallery))
            }
        }

        AnimatedVisibility(
            visible = !isLoading && !isFailure,
            modifier = Modifier
                .constrainAs(cameraBtnRef) {
                    centerVerticallyTo(parent, 0f)
                    start.linkTo(photoRef.end)
                    end.linkTo(parent.end)
                }
                .padding(2.dp)
                .size(fabSize)) {
            FloatingActionButton(
                onClick = { onLaunchCameraClick() },
                shape = fabShape
            ) {
                Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = stringResource(id = R.string.desc_photo_from_camera))
            }
        }
        

        //Retry
        AnimatedVisibility(
            visible = !isLoading,
            modifier = Modifier
                .constrainAs(reloadRef) {
                    centerVerticallyTo(parent, 1f)
                    start.linkTo(parent.start)
                    end.linkTo(photoRef.start)
                }
                .padding(2.dp)
                .size(fabSize)) {
            FloatingActionButton(
                onClick = { onReloadPhotoClick() },
                shape = fabShape
            ) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = stringResource(id = R.string.desc_reload_photo))
            }
        }

    }
}

@Preview
@Composable
fun J() {
    PhotoPicker(
        photoBitmap = null,
        placeholder = Icons.Outlined.InsertPhoto,
        onBrowseGalleryClick = { /*TODO*/ },
        onDeletePhotoClick = { /*TODO*/ },
        isLoading = true,
        modifier = Modifier
            .padding(16.dp)
            .size(200.dp),
    )
}