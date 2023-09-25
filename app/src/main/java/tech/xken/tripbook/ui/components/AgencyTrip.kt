@file:OptIn(ExperimentalMaterialApi::class)

package tech.xken.tripbook.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.DepartureBoard
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import tech.xken.tripbook.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgencyTrip() {
    ConstraintLayout(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        val (infoRef, shareRef, priceRef) = createRefs()

        Card(
            modifier = Modifier
                .padding(4.dp)
                .constrainAs(infoRef) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    width = Dimension.fillToConstraints
                    centerVerticallyTo(parent)
                }
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.Business,
                        contentDescription = stringResource(id = R.string.desc_agency_logo),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(64.dp)
                    )
                    Column(
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "Avenir Voyage Express",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Explore the universe!",
                            style = MaterialTheme.typography.caption,
                            fontStyle = FontStyle.Italic
                        )
                    }

                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Chip(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 4.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Place, contentDescription = null)
                        },
                        colors = ChipDefaults.chipColors(leadingIconContentColor = colors.onSurface)
                    ) {
                        Text("2 Stations")
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = colors.onSurface)
                Row(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Chip(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ChipDefaults.chipColors(leadingIconContentColor = colors.onSurface),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.DepartureBoard,
                                contentDescription = null
                            )
                        }
                    ) {
                        Text("5:00 am")
                    }
                    Chip(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 1.dp)
                    ) {
                        Text("9:00 am")
                    }
                    Chip(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 1.dp)
                    ) {
                        Text("9:30 pm")
                    }
                }
            }

        }

        IconButton(
            modifier = Modifier
                .padding(4.dp)
                .constrainAs(shareRef) {
                    end.linkTo(infoRef.end)
                    top.linkTo(infoRef.top)
                },
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(id = R.string.desc_agency_trip_details),
                tint = colors.onSurface
            )
        }


        Card(
            modifier = Modifier
                .padding(4.dp)
                .constrainAs(priceRef) {
                    end.linkTo(infoRef.end)
                    width = Dimension.wrapContent
                    centerVerticallyTo(infoRef)
                },
            backgroundColor = colors.primary,
            shape = MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(100),
                topEnd = CornerSize(0),
                bottomEnd = CornerSize(0),
                bottomStart = CornerSize(0)
            )
        ) {
            Text(
                "4 525 FCFA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

    }
}

@Composable
@Preview(
    device = "spec:width=392.7dp,height=850.9dp,dpi=440", showSystemUi = true,
    showBackground = true
)
fun AgencyTripPreview() {
    AgencyTrip()
}