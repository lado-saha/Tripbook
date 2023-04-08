package tech.xken.tripbook.ui.components

import android.telephony.PhoneNumberUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import java.util.*


import androidx.compose.material.Icon

import androidx.compose.material.Text
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import tech.xken.tripbook.data.models.*
import tech.xken.tripbook.data.models.Scanner

/*@ExperimentalFoundationApi
@Composable
@Preview
fun SolarFarmItemPreview() {
    Column {
        for (i in 0..10) {
            SolarPlantItem(
                modifier = Modifier,
                profile = SolarPlantProfile(
                    id = UUID.randomUUID().toString(),
                    name = "Project Solaris-1",
                    owner = "Jonathan",
                    location = Location.DEFAULT,
                    startDate = SimpleDate(Calendar.getInstance()),
                    endDate = SimpleDate(Calendar.getInstance())
                )
            )
        }
    }
}*/


/*
@ExperimentalFoundationApi
@Composable
fun SolarPlantItem(
    modifier: Modifier = Modifier,
    profile: SolarPlantProfile,
    isMarked: Boolean = false,
    onClick: (SolarPlantProfile) -> Unit = {},
    onLongClick: (SolarPlantProfile) -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .combinedClickable(
                onLongClick = { onLongClick(profile) },
                onClick = { onClick(profile) },
                onLongClickLabel = stringResource(R.string.dsc_mark_item).caps
            ),
        elevation = if (isMarked) 8.dp else 0.dp
    ) {
        ConstraintLayout(
            modifier = modifier.fillMaxWidth()
        ) {

            val markTransition = updateTransition(
                targetState = isMarked,
                label = null,
            )
            val (iconRef, tagsRef, nameRef, locationRef, dateRef, statusRef, selectedCheckRef, isLocalRef) = createRefs()


            */
/*val markedIconSize = sizeAnimation(
                transition = markTransition,
                duration = 150,
                labelRes = R.string.dbg_size_animation
            ) {
                when (it) {
                    true -> 18.dp
                    false -> 0.dp
                }
            }*//*

            if (markedIconSize != 0.dp)
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.constrainAs(selectedCheckRef) {
                        start.linkTo(anchor = iconRef.start, margin = 18.dp)
                        end.linkTo(iconRef.end)
                        bottom.linkTo(iconRef.bottom)
                        top.linkTo(iconRef.top, margin = 18.dp)
                    }
                        .padding(2.dp)
                        .background(color = colors.background, shape = CircleShape)
                        .size(markedIconSize)
                )
            if (profile.isLocal)
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier
                        .constrainAs(isLocalRef) {
                            end.linkTo(parent.end, margin = 4.dp)
                            top.linkTo(parent.top, margin = 4.dp)
                        }
                        .background(color = colors.background, shape = CircleShape)
                        .size(16.dp)
                )

            Text(
                text = profile.name,
                modifier = Modifier
                    .constrainAs(nameRef) {
                        start.linkTo(iconRef.end)
                        end.linkTo(
                            parent.end
                        )
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                    .padding(all = 2.dp),
                style = Typography.h6
            )

            if (profile.isFinalized)
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.dsc_ic_completed),
                    modifier = Modifier
                        .constrainAs(statusRef) {
                            end.linkTo(iconRef.end)
                            start.linkTo(iconRef.start)
                            top.linkTo(iconRef.top)
                            bottom.linkTo(iconRef.bottom)
                        }
                        .padding(all = 4.dp)
                        .size(16.dp)
                )



            Text(
                text = profile.location.toString(),
                modifier = Modifier
                    .constrainAs(locationRef) {
                        start.linkTo(iconRef.end)
                        end.linkTo(parent.end)
                        top.linkTo(nameRef.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(all = 2.dp),
                style = Typography.caption
            )

            Text(
                text = SimpleDateFormat(
                    stringResource(R.string.pattern_date_format),
                    Locale.getDefault()
                ).format(Date()),
                modifier = Modifier
                    .constrainAs(dateRef) {
                        start.linkTo(iconRef.end)
                        top.linkTo(locationRef.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(all = 2.dp),
                style = Typography.caption
            )

        }
    }
}

*/

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScannerSearchResultItem(
    scanner: Scanner,
    isSelected: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    isRecruited: Boolean = false,
) {
    Card {
        ConstraintLayout(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = { onLongClick(scanner.bookerID) },
                    onClick = { onClick(scanner.bookerID) })
        ) {
            val (photoRef, phoneRef, tagsRef, isRecruitedRef, selectionStatusRef, nameRef, isSelectedRef) = createRefs()

            AnimatedVisibility(
                isSelected,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .constrainAs(selectionStatusRef) {
                        centerVerticallyTo(photoRef)
                        start.linkTo(parent.start)
                        end.linkTo(photoRef.start)
                    }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null
                )
            }

            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(50.dp)
                    .constrainAs(photoRef) {
//                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        start.linkTo(if (isSelected) selectionStatusRef.end else parent.start)
                    })

            if (isRecruited)
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(isRecruitedRef) {
                            start.linkTo(anchor = photoRef.start, margin = 35.dp)
                            end.linkTo(photoRef.end)
                            bottom.linkTo(photoRef.bottom)
                            top.linkTo(photoRef.top, margin = 35.dp)
                        }
                        .padding(4.dp)
                        .background(color = MaterialTheme.colors.background, shape = CircleShape)
                        .size(16.dp)
                )

            Text(
                text = scanner.booker.name!!,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .constrainAs(nameRef) {
                        top.linkTo(photoRef.top)
                        bottom.linkTo(phoneRef.top)
                        start.linkTo(photoRef.end)
                        end.linkTo(isSelectedRef.start)
                        width = Dimension.fillToConstraints
                    },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = PhoneNumberUtils.formatNumber(
                    "${scanner.booker.phoneCode}${scanner.booker.phone}",
                    codeCountryMap[scanner.booker.phoneCode]
                ),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .constrainAs(phoneRef) {
                        start.linkTo(photoRef.end)
                        end.linkTo(isSelectedRef.start)
                        top.linkTo(nameRef.bottom)
                        bottom.linkTo(photoRef.bottom)
                        width = Dimension.fillToConstraints
                    },
                style = MaterialTheme.typography.caption
            )

            Checkbox(
                checked = true,
                onCheckedChange = { onCheckedChange(it) },
                modifier = Modifier
                    .constrainAs(isSelectedRef) {
                        centerVerticallyTo(photoRef)
                        bottom.linkTo(tagsRef.top)
                        end.linkTo(parent.end)
                    },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary,
                )
            )
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .constrainAs(tagsRef) {
                        start.linkTo(photoRef.end)
                        end.linkTo(parent.end)

                        top.linkTo(phoneRef.bottom)
                        width = Dimension.fillToConstraints
                    },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(18.dp),
                    imageVector = if (scanner.booker.gender == Gender.MALE) Icons.Filled.Male else Icons.Filled.Female,
                    contentDescription = null
                )

                //TODO: URgent do the commented stuff scanne jobs
//                Icon(
//                    modifier = Modifier
//                        .padding(horizontal = 2.dp)
//                        .size(16.dp),
//                    imageVector = StationDefJobs.valueOf(scanner!!).icon,
//                    contentDescription = null
//                )

                if (scanner.isSuspended == true)
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(16.dp),
                        imageVector = Icons.Default.Block,
                        tint = MaterialTheme.colors.error,
                        contentDescription = null
                    )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerSearchResultItemPrev(
) {
    ScannerSearchResultItem(
        scanner = Scanner("dfsfd", "dfsadf", null, true).apply {
            booker = Booker(
                "dsds",
                "Joe Dalton",
                "Male",
                null,
                null,
                null,
                "6669845",
                "237",
                null,
                null,
                null,
                null
            )
        },
        onCheckedChange = {},
        onClick = {},
        onLongClick = {},
        isSelected = false,
        isRecruited = true
    )
}