package tech.xken.tripbook.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.FileUiState
import tech.xken.tripbook.data.models.ImageUiState
import tech.xken.tripbook.data.models.PDFUiState


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilePicker(
    uis: FileUiState,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    elevation: Dp = 1.dp,
    border: BorderStroke? = null,
    onUiStateChange: (new: FileUiState) -> Unit,
    onOpenClick: () -> Unit,
    onEditClick: () -> Unit,
    onInfoClick: () -> Unit,
    onHideClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUndoClick: () -> Unit
) {
    Card(
        shape = shape,
        elevation = elevation,
        border = border,
        modifier = modifier
//        backgroundColor = if (uis.isMarked || uis.hasFailed) DrawerDefaults.scrimColor else MaterialTheme.colors.surface
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (toolboxRef, titleRef, loadingRef, previewRef) = createRefs()
            val isSuccess = !(uis.isLoading || uis.isFailure)

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .constrainAs(toolboxRef) {
                        centerHorizontallyTo(parent, 1f)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (uis.isDeletable && isSuccess && (uis.localUri != Uri.EMPTY || uis.url != null))
                    IconButton(
                        onClick = { onDeleteClick() },
                        modifier = Modifier.padding(1.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colors.error
                        )
                    }

                if (uis.canUndo && isSuccess)
                    IconButton(
                        onClick = { onUndoClick() },
                        modifier = Modifier.padding(1.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Undo,
                            contentDescription = stringResource(id = R.string.msg_undo_all_changes),
                        )
                    }

                if (uis.hasInfo)
                    IconButton(
                        onClick = { onInfoClick() },
                        modifier = Modifier.padding(1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                    }

                if (isSuccess)
                    IconButton(
                        onClick = {
                            onHideClick()
                        },
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Icon(
                            imageVector = if (uis.isContentHidden) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                        )
                    }

                when (uis) {
                    is ImageUiState -> {
                        if (isSuccess)
                            IconButton(
                                onClick = { onOpenClick() },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Fullscreen,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                    }

                    is PDFUiState -> {
                        if (isSuccess)
                            IconButton(
                                onClick = { onOpenClick() },
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.OpenInNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                    }
                }

                IconButton(
                    onClick = { onEditClick() },
                    modifier = Modifier.padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            Text(
                text = uis.title,
                modifier = Modifier
                    .constrainAs(titleRef) {
                        top.linkTo(toolboxRef.bottom)
                        centerHorizontallyTo(parent)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 8.dp, bottom = 4.dp),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
            )

            if (!uis.isContentHidden) {
                when (uis) {
                    is ImageUiState -> {
                        AsyncImage(
                            modifier = Modifier
                                .constrainAs(previewRef) {
                                    top.linkTo(titleRef.bottom)
                                    bottom.linkTo(if (uis.isLoading) loadingRef.top else parent.bottom)
                                    centerHorizontallyTo(parent)
                                }
                                .padding(4.dp)
                                .fillMaxWidth()
                                .heightIn(0.dp, 200.dp)
                                .clip(RoundedCornerShape(40.dp)),
                            model = if (uis.localUri == Uri.EMPTY) uis.url else uis.localUri,
                            placeholder = rememberVectorPainter(image = uis.placeholder),
                            contentDescription = uis.title,
                            error = rememberVectorPainter(image = uis.placeholder),
                            onLoading = {
                                onUiStateChange(
                                    uis.copy(
                                        isLoading = true,
                                        isFailure = false,
                                    )
                                )
                            },
                            onError = {
                                onUiStateChange(
                                    uis.copy(
                                        isLoading = false,
                                        isFailure = true,
                                    )
                                )
                            },
                            onSuccess = {
                                onUiStateChange(
                                    uis.copy(
                                        isLoading = false,
                                        isFailure = false
                                    )
                                )
                            },
                            fallback = rememberVectorPainter(image = uis.placeholder),
                            contentScale = ContentScale.Crop
                        )
                    }

                    is PDFUiState -> {
                        Row(
                            modifier = Modifier
                                .constrainAs(previewRef) {
                                    top.linkTo(titleRef.bottom)
                                    bottom.linkTo(if (uis.isLoading) loadingRef.top else parent.bottom)
                                    centerHorizontallyTo(parent)
                                }
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(32.dp),
                                model = if (uis.localUri == Uri.EMPTY) uis.url else uis.localUri,
                                placeholder = rememberVectorPainter(image = Icons.Filled.PictureAsPdf),
                                onLoading = {
                                    onUiStateChange(
                                        uis.copy(
                                            isLoading = true,
                                            isFailure = false,
                                        )
                                    )
                                },
                                contentDescription = uis.title,
                                error = rememberVectorPainter(image = Icons.Filled.PictureAsPdf),
                                onError = {
                                    onUiStateChange(
                                        uis.copy(
                                            isLoading = false,
                                            isFailure = true,
                                        )
                                    )
                                },
                                onSuccess = {
                                    onUiStateChange(
                                        uis.copy(
                                            isLoading = false,
                                            isFailure = false
                                        )
                                    )
                                },
                            )

                            DashboardSubItem(
                                modifier = Modifier.padding(2.dp),
                                isError = uis.isFailure || (uis.url.isNullOrBlank() || uis.localUri == Uri.EMPTY),
                                positiveText = "Ok",
                                errorText = "Upload a pdf file"
                            )
                        }
                    }
                }
            }

            if (uis.isLoading)
                LinearProgressIndicator(modifier = Modifier
                    .constrainAs(loadingRef) {
                        top.linkTo(previewRef.bottom)
                        centerHorizontallyTo(parent)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(top = 2.dp)
                    .fillMaxWidth())

        }
    }
}

//@Preview
//@Composable
//fun J() {
//    PhotoPicker(
//        photoBitmap = null,
//        placeholder = Icons.Outlined.InsertPhoto,
//        onBrowseGalleryClick = { /*TODO*/ },
//        onDeletePhotoClick = { /*TODO*/ },
//        isLoading = true,
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth()
//            .size(200.dp),
//    )
//}