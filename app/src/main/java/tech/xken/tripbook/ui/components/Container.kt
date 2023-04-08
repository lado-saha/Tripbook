package tech.xken.tripbook.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.Job
import tech.xken.tripbook.data.models.Job.Companion.DefaultJobs
import tech.xken.tripbook.data.models.StationJob
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
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = DefaultJobs.valueOf(stationJob.jobId).icon,
                    contentDescription = null,
                    modifier = Modifier
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
                        text = stationJob.job!!.name!!.titleCase,
                        style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(4.dp)
                    )
                    val color =
                        if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error

                    Text(
                        text = errorText() ?: stationJob.subtitle,
                        style = MaterialTheme.typography.caption.copy(color = color),
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 4.dp,
                            end = 4.dp,
                            start = 6.dp
                        )
                    )

                }
            }
            Column(modifier = Modifier.padding(2.dp)) {
                Text(
                    stringResource(id = R.string.lb_wiki),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    stationJob.job!!.wiki!!.caps,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(2.dp)
                )
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
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            logoIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.weight(0.1f),
                )
            }

            Text(
                text = text,
                modifier = Modifier.weight(0.8f, true),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            IconButton(modifier = Modifier.weight(0.1f), onClick = {
                onVisibilityClick(isVisible)
            }) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }
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
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
//        border = if (errorText() != null) BorderStroke(1.dp, MaterialTheme.colors.error) else null,
        onClick = {
            onClick()
        },
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = DefaultJobs.valueOf(job.id.replace("u_", "")).icon,
                    contentDescription = null,
                    modifier = Modifier
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
                        text = job.name!!.titleCase,
                        style = MaterialTheme.typography.h6.copy(fontSize = 16.sp),
                        modifier = Modifier.padding(4.dp)
                    )
//                    val color =
//                        if (errorText() == null) MaterialTheme.typography.caption.color else MaterialTheme.colors.error

                    Text(
                        text = job.wiki!!.caps,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(
                            top = 0.dp,
                            bottom = 4.dp,
                            end = 4.dp,
                            start = 6.dp
                        )
                    )

                }
            }
            OutlinedButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )
                Text(stringResource(id = R.string.lb_add_job).titleCase)
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