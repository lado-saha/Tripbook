package tech.xken.tripbook.ui.components

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.xken.tripbook.data.models.agency.Job
import tech.xken.tripbook.data.models.agency.StationJob
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.titleCase

/**
 * [StationJob]
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StationJobItem(
    modifier: Modifier = Modifier,
    stationJob: StationJob,
    errorText: @Composable () -> String? = { null },
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null,
        onClick = {
            onClick()
        },
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                Icon(
//                    imageVector = Job.jobIcon[stationJob.jobId]!!,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .weight(0.1f)
//                )
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.9f, true),
                    horizontalAlignment = Alignment.Start
                ) {
//                    Text(
//                        text = stringResource(id = stationJob.job!!.name!!).titleCase,
//                        style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
//                        modifier = Modifier.padding(4.dp)
//                    )
                    val color =
                        if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error

                    Text(
                        text = errorText() ?: stationJob.subtitle,
                        style = MaterialTheme.typography.caption.copy(color = color),
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 0.dp,
                            end = 4.dp,
                            start = 6.dp
                        )
                    )

                }
            }
            Column(modifier = Modifier.padding(2.dp)) {
//                Text(
//                    stringResource(id = stationJob.job!!.shortWiki!!).caps,
//                    style = MaterialTheme.typography.caption,
//                    modifier = Modifier.padding(2.dp),
//                )
            }
        }
    }
}

@Composable
fun ListHeader(
    modifier: Modifier = Modifier,
    text: String,
    isVisible: Boolean = true,
    logoIcon: ImageVector?,
    onVisibilityClick: (isVisible: Boolean) -> Unit,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        logoIcon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.1f)
                    .size(16.dp),
            )
        }

        Text(
            text = text,
            modifier = Modifier.weight(0.8f, true),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )

        Icon(
            modifier = Modifier
                .weight(0.1f)
                .clickable { onVisibilityClick(isVisible) },
            imageVector = if (isVisible) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
            contentDescription = null,
        )
    }
}


/**
 * [Job]
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JobItem(
    modifier: Modifier = Modifier,
    job: Job,
    onAddClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 0.dp
//        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null,
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                Icon(
//                    imageVector = job.icon!!,
//                    contentDescription = null,
//                    modifier = Modifier
//                )
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.8f, true),
                    horizontalAlignment = Alignment.Start
                ) {
//                    Text(
//                        text = stringResource(id = job.name!!).titleCase,
//                        style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
//                        modifier = Modifier.padding(4.dp)
//                    )
//                    val color =
//                        if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error

//                    Text(
//                        text = stringResource(id = job.shortWiki!!).caps,
//                        style = MaterialTheme.typography.caption,
//                        modifier = Modifier.padding(
//                            top = 0.dp,
//                            bottom = 4.dp,
//                            end = 4.dp,
//                            start = 6.dp
//                        )
//                    )

                }
                IconButton(
                    onClick = { onAddClick() },
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    subtitle: String,
    errorText: @Composable () -> String? = { null },
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null,
        enabled = isEnabled,
        onClick = {
            onClick()
        },
        elevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = imageVector, contentDescription = null, modifier = Modifier
                        .padding(4.dp)
                        .weight(0.1f)
                )
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(0.9f, true),
                    horizontalAlignment = Alignment.Start

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
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 4.dp,
                            end = 4.dp,
                            start = 6.dp
                        )
                    )

                    AnimatedVisibility(
                        visible = isLoading,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        LinearProgressIndicator()
                    }

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainMenuItem(
    modifier: Modifier = Modifier,
    imageBitmap: Bitmap? = null,
    imageVector: ImageVector? = null,
    placeholder: ImageVector = Icons.Outlined.InsertPhoto,
    title: String,
    subtitle: String,
    isLoading: Boolean = false,
    errorText: @Composable () -> String? = { subtitle },
    onClick: () -> Unit,
) {

    Card(
        modifier = modifier,
        onClick = { onClick() },
        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                        .size(75.dp)
                        .clip(RoundedCornerShape(30f))
                )

            } else if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                        .size(75.dp)
                        .clip(RoundedCornerShape(30f))
                )
            } else {
                Icon(
                    imageVector = placeholder, contentDescription = null, modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                        .size(75.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.8f, true),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                val color =
                    if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error
                Text(
                    text = errorText() ?: subtitle,
                    style = MaterialTheme.typography.caption.copy(
                        color = color,
                        textAlign = TextAlign.Start
                    ),
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                )

                AnimatedVisibility(
                    visible = isLoading,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    LinearProgressIndicator()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    icon: ImageVector,
    @StringRes contentDesc: Int? = null,
    isSelected: Boolean = false,
    alertText: String  = ""
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.background
    Row(
        modifier = modifier.background(
            backgroundColor,
            RoundedCornerShape(topEnd = 50f, bottomEnd = 50f)
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc?.let { stringResource(id = it) },
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 24.dp)
                .size(24.dp)
        )
        Text(
            text = stringResource(id = title).caps,
            modifier = Modifier.weight(0.8f, fill = true),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        if (alertText.isNotBlank())
            Chip(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(20.dp),
                enabled = false,
            ) {
                Text(alertText)
            }
    }
}

