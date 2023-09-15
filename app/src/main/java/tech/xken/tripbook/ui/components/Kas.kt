package tech.xken.tripbook.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.Dispatchers
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.SortField
import tech.xken.tripbook.ui.theme.ColorWarn
import kotlin.coroutines.CoroutineContext

data class InfoDialogUiState(
    val mainIcon: ImageVector = Icons.Filled.Image,
    val title: String = "",
    val text: AnnotatedString = AnnotatedString(""),
    val positiveText: String = "",
    val otherText: String? = "",
    val isNegative: Boolean = false
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NetworkStateIndicator(
    modifier: Modifier = Modifier,
    context: CoroutineContext = Dispatchers.Main,
    onClick: () -> Unit,
    isOnline: () -> Boolean,
) {

}


@Composable
fun InfoDialog(
    uis: InfoDialogUiState,
    onCloseClick: () -> Unit,
    onPositiveClick: () -> Unit,
    onOtherClick: () -> Unit = {},
    mainIconTint: Color = MaterialTheme.colors.primary,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Card {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())

            ) {
                val (btnCloseRef, mainIconRef, btnPositiveRef, btnOtherRef, titleRef, textRef) = createRefs()
                IconButton(
                    onClick = { onCloseClick() },
                    modifier = Modifier.constrainAs(btnCloseRef) {
                        centerHorizontallyTo(parent, 1f)
                        top.linkTo(parent.top)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(id = R.string.desc_close_dialog),

                        )
                }

                Icon(
                    imageVector = uis.mainIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(mainIconRef) {
                            centerHorizontallyTo(parent, 0f)
                            top.linkTo(parent.top)
                        }
                        .padding(4.dp)
                        .size(64.dp),
                    tint = mainIconTint
                )

                Text(
                    text = uis.title,
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            centerHorizontallyTo(parent, 0f)
                            top.linkTo(mainIconRef.bottom)
                        }
                        .padding(start = 4.dp, end = 4.dp, top = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    modifier = Modifier
                        .constrainAs(textRef) {
                            centerHorizontallyTo(parent)
                            top.linkTo(titleRef.bottom)
                        }
                        .padding(start = 4.dp, top = 4.dp, bottom = 8.dp, end = 4.dp),
                    text = uis.text,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Justify
                )

                Button(
                    onClick = { onPositiveClick() },
                    modifier = Modifier
                        .constrainAs(btnPositiveRef) {
                            centerHorizontallyTo(parent, 0f)
                            top.linkTo(textRef.bottom)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(4.dp), shape = MaterialTheme.shapes.small.copy(CornerSize(30))
                ) {
                    Text(uis.positiveText)
                }

                if (!uis.otherText.isNullOrBlank())
                    Button(
                        onClick = { onOtherClick() },
                        modifier = Modifier
                            .constrainAs(btnOtherRef) {
                                centerVerticallyTo(btnPositiveRef)
                                start.linkTo(btnPositiveRef.end)
                            }
                            .padding(start = 16.dp),
                        shape = MaterialTheme.shapes.small.copy(CornerSize(30)),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.run { if (uis.isNegative) error else background }),
                        elevation = ButtonDefaults.elevation(4.dp)
                    ) {
                        Text(uis.otherText)
                    }
            }
        }
    }
}

@Composable
fun SortingDialog(
    onDismiss: () -> Unit,
    sortFields: List<SortField>,
    selectedField: SortField?,
    onFieldClick: (SortField) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
    ) {
        Card {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())

            ) {
                val (btnCloseRef, mainIconRef, titleRef, textRef) = createRefs()
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.constrainAs(btnCloseRef) {
                        centerHorizontallyTo(parent, 1f)
                        top.linkTo(parent.top)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(id = R.string.desc_close_dialog),

                        )
                }

                Icon(
                    imageVector = Icons.Outlined.Sort,
                    contentDescription = null,
                    modifier = Modifier
                        .constrainAs(mainIconRef) {
                            centerHorizontallyTo(parent, 0f)
                            top.linkTo(parent.top)
                        }
                        .padding(4.dp)
                        .size(64.dp),
                )

                Text(
                    text = stringResource(id = R.string.lb_sort_by),
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            centerHorizontallyTo(parent, 0f)
                            top.linkTo(mainIconRef.bottom)
                        }
                        .padding(start = 4.dp, end = 4.dp, top = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.ExtraBold,
                )

                Column(
                    modifier = Modifier
                        .constrainAs(textRef) {
                            centerHorizontallyTo(parent)
                            top.linkTo(titleRef.bottom)
                            bottom.linkTo(parent.bottom)
//                            width = Dimension.fillToConstraints
//                            height = Dimension.wrapContent
                        }
                        .padding(start = 4.dp, top = 4.dp, bottom = 8.dp, end = 4.dp),
                ) {
                    sortFields.forEach { field ->
                        SortItem(
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth(),
                            field = field,
                            isSelected = field.nameRes == selectedField?.nameRes,
                            onAscendingClick = {
                                onFieldClick(it.copy(ascending = true))
                            },
                            onDescendingClick = {
                                onFieldClick(it.copy(ascending = false))
                            }
                        )
                    }
                }

            }
        }
    }
}


data class DashboardItemUiState(
    val mainIcon: ImageVector = Icons.Filled.Image,
    val title: String = "",
    val isClickable: Boolean = true,
    val isRefreshable: Boolean = false,
    val isHelpable: Boolean = false,
    val isDeletable: Boolean = false,
    val hasOther: Boolean = false,
    val isLoading: Boolean = false,
    val isMarkable: Boolean = false,
    val isMarked: Boolean = false,
    val isFailure: Boolean = false
)

/**
 * @property isMarkable Means it can be longed clicked and marked
 */
data class DashboardItemNoIconUiState(
    val title: String = "",
    val isClickable: Boolean = true,
    val isMarkable: Boolean = false,
    val isRefreshable: Boolean = false,
    val isHelpable: Boolean = false,
    val isDeletable: Boolean = false,
    val hasOther: Boolean = false,
    val isLoading: Boolean = false,
    val isMarked: Boolean = false,
    val isFailure: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun DashboardItem(
    modifier: Modifier = Modifier,
    uis: DashboardItemUiState = DashboardItemUiState(),
    mainIconColor: Color = MaterialTheme.colors.primary,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 1.dp,
    mainIconSize: Dp = 32.dp,
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onOtherClick: () -> Unit = {},
    otherIcon: ImageVector? = null,
    onRetryClick: () -> Unit = {},
    otherIconTint: Color = MaterialTheme.colors.onSurface,
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        elevation = elevation,
        border = border,
        modifier = modifier,
        onClick = {
            onLongClick()
        },
        enabled = uis.isMarkable,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            val (toolboxRef, contentRef, mainIconRef, titleRef, loadingRef, markRef) = createRefs()

            if (!uis.isLoading && !uis.isFailure && !uis.isMarked)
                Row(
                    modifier = Modifier
                        .constrainAs(toolboxRef) {
                            centerHorizontallyTo(parent, 1f)
                            top.linkTo(parent.top)
                        }
                        .padding(top = 4.dp, end = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (uis.isDeletable)
                        IconButton(
                            onClick = { onDeleteClick() },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colors.error
                            )
                        }

                    if (uis.hasOther)
                        IconButton(
                            onClick = { onOtherClick() },
                            modifier = Modifier.padding(2.dp),
                        ) {
                            otherIcon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    tint = otherIconTint
                                )
                            }
                        }
                    //tweak it
                    if (uis.isRefreshable)
                        IconButton(
                            onClick = { onRefreshClick() },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                        }

                    if (uis.isHelpable)
                        IconButton(
                            onClick = { onHelpClick() },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        }


                    if (uis.isClickable)
                        IconButton(
                            onClick = { onClick() },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }

                }

            Icon(
                imageVector = uis.mainIcon,
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(mainIconRef) {
                        centerHorizontallyTo(parent, 0f)
                        top.linkTo(parent.top)
                    }
                    .padding(4.dp)
                    .size(48.dp),
                tint = mainIconColor
            )

            Text(
                text = uis.title,
                modifier = Modifier
                    .constrainAs(titleRef) {
                        centerHorizontallyTo(parent, 0f)
                        top.linkTo(mainIconRef.bottom)
                    }
                    .padding(start = 16.dp, end = 4.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .constrainAs(contentRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(titleRef.bottom)
                        bottom.linkTo(if (uis.isLoading) loadingRef.top else parent.bottom)
                        height = Dimension.wrapContent
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                    .fillMaxWidth()
            ) {
                if (!uis.isLoading && !uis.isFailure)
                    content()
            }
            if (uis.isLoading)
                LinearProgressIndicator(modifier = Modifier
                    .constrainAs(loadingRef) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(top = 2.dp)
                    .fillMaxWidth())

            if (uis.isMarked || uis.isFailure)
                Card(
                    modifier = Modifier.constrainAs(markRef) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.percent(0.5f)
                    },
                    elevation = 0.dp,
                    backgroundColor = if (uis.isMarked) MaterialTheme.colors.primary.copy(alpha = 0.5f) else MaterialTheme.colors.error.copy(
                        alpha = 0.5f
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(0.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uis.isFailure) {
                            IconButton(
                                onClick = { onRetryClick() },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(
                                        id = R.string.desc_failed_retry
                                    )
                                )
                            }
                        }
                        if (uis.isMarked)
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(
                                    id = R.string.desc_is_selected
                                )
                            )
                    }
                }


        }
    }

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardItemNoIcon(
    modifier: Modifier = Modifier,
    uis: DashboardItemNoIconUiState = DashboardItemNoIconUiState(),
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 1.dp,
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onOtherClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    otherIcon: ImageVector? = null,
    otherIconTint: Color = MaterialTheme.colors.onSurface,
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        shape = shape,
        elevation = elevation,
        border = border,
        modifier = modifier,
        onClick = {
            onLongClick()
        },
//        backgroundColor = if (uis.isMarked || uis.hasFailed) DrawerDefaults.scrimColor else MaterialTheme.colors.surface
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (toolboxRef, contentRef, titleRef, loadingRef, markRef) = createRefs()

            if (!uis.isLoading && !uis.isFailure && !uis.isMarked)
                Row(
                    modifier = Modifier
                        .constrainAs(toolboxRef) {
                            centerHorizontallyTo(parent, 1f)
                            top.linkTo(parent.top)
                            width = Dimension.fillToConstraints
                        }
                        .padding(top = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {

                    if (uis.isDeletable)
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

                    if (uis.hasOther)
                        IconButton(
                            onClick = { onOtherClick() },
                            modifier = Modifier.padding(1.dp),
                        ) {
                            otherIcon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    tint = otherIconTint
                                )
                            }
                        }
                    //tweak it
                    if (uis.isRefreshable)
                        IconButton(
                            onClick = { onRefreshClick() },
                            modifier = Modifier.padding(1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                        }

                    if (uis.isHelpable)
                        IconButton(
                            onClick = { onHelpClick() },
                            modifier = Modifier.padding(1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        }


                    if (uis.isClickable)
                        IconButton(
                            onClick = { onClick() },
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
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
//                        start.linkTo(parent.start)
//                        end.linkTo(toolboxRef.start)
//                        centerVerticallyTo(toolboxRef)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 8.dp, end = 2.dp),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
            )

            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .constrainAs(contentRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(titleRef.bottom)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.wrapContent
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                    .fillMaxWidth()
            ) {
                if (!uis.isLoading && !uis.isFailure)
                    content()
            }

            if (uis.isLoading)
                LinearProgressIndicator(modifier = Modifier
                    .constrainAs(loadingRef) {
                        centerHorizontallyTo(parent)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(top = 2.dp)
                    .fillMaxWidth())


            if (uis.isMarked || uis.isFailure)
                Card(
                    modifier = Modifier.constrainAs(markRef) {
                        centerVerticallyTo(parent)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.percent(0.5f)
                    },
                    elevation = 0.dp,
                    backgroundColor = if (uis.isMarked) MaterialTheme.colors.primary.copy(alpha = 0.5f) else MaterialTheme.colors.error.copy(
                        alpha = 0.5f
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(0.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uis.isFailure) {
                            IconButton(
                                onClick = { onRetryClick() },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = stringResource(
                                        id = R.string.desc_failed_retry
                                    )
                                )
                            }
                        }
                        if (uis.isMarked)
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(
                                    id = R.string.desc_is_selected
                                )
                            )
                    }
                }

        }
    }

}

@Composable
fun DashboardSubItem(
    modifier: Modifier = Modifier,
    isError: Boolean?,
    positiveText: String,
    errorText: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.NavigateNext,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(16.dp),
            tint = when (isError) {
                false -> MaterialTheme.colors.primary
                null -> ColorWarn
                true -> MaterialTheme.colors.error
            }

        )
        Text(
            when (isError) {
                false -> positiveText
                else -> errorText
            }
        )
    }
}