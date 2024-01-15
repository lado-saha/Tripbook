package tech.xken.tripbook.ui.components

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.SortField
import tech.xken.tripbook.domain.caps


/**
 * @param onFieldClick We need to specifically move the new field to the 0th position in the list
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    fields: List<SortField>,
    onFieldClick: (new: SortField) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int = R.string.lb_search,
    fieldsLazyListState: LazyListState = rememberLazyListState(),
    onBackClick: () -> Unit,
    onClearQueryClick: () -> Unit,
    isError: () -> Boolean = { false },
    onMoreClick: (() -> Unit)? = null
//    isFiltersVisible: Boolean,
//    filters: List<Filter>,
//    onFilterClick: (new: Filter) -> Unit,
//    filtersLazyListState: LazyListState = rememberLazyListState(),
) {
    Card(
        modifier = modifier.padding(2.dp),
        shape = RoundedCornerShape(50f),
        elevation = if (query.isBlank()) 1.dp else 1.dp,
        border = if (query.isBlank()) null else BorderStroke(
            width = 1.dp,
            color = if (isError()) MaterialTheme.colors.error else MaterialTheme.colors.primary
        )
    ) {
        Column(
            modifier = Modifier
        ) {
            OutTextField(
                modifier = Modifier.fillMaxWidth(),
//                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                value = query,
                placeholder = {
                    Text(
                        text = stringResource(placeholder).caps,
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                onValueChange = {
                    onQueryChange(it)
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        if (query.isNotBlank())
                            IconButton(
                                onClick = { onClearQueryClick() },
                                modifier = Modifier.padding(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = null
                                )
                            }
                        if (onMoreClick != null)
                            IconButton(
                                onClick = { onMoreClick() },
                                modifier = Modifier.padding(0.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.MoreVert,
                                    contentDescription = null,
                                    tint = LocalContentColor.current
                                )
                            }
                    }
                },
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onBackClick() },
                            shape = RoundedCornerShape(percent = 100),
                            modifier = Modifier
                                .padding(start = 8.dp, end = 2.dp)
                                .animateContentSize()
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp),
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.dsc_btn_cancel_search),
                            )
                            Text(
                                stringResource(id = fields.first().nameRes).caps,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                singleLine = true
            )
            //We only show this when the search text is empty
            AnimatedVisibility(query.isBlank()) {
                LazyRow(
                    state = fieldsLazyListState,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth()
                ) {
                    items(
                        items = fields.subList(1, fields.size)
                    ) { field ->
                        Button(
                            //Index + 1 Since we are excluding the already selected field which is at first index
                            onClick = { onFieldClick(field) },
                            shape = RoundedCornerShape(percent = 100),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .animateContentSize(),
                        ) {
                            Text(
                                text = stringResource(id = field.nameRes).caps,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }
            }
        }
        /*Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            thickness = 2.dp
        )

        AnimatedVisibility(isFiltersVisible) {
            LazyRow(
                state = filtersLazyListState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    IconButton(
                        modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                        onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = null
                        )
                    }
                }
                items(
                    items = filters
                ) { filter ->
                    OutlinedButton(
                        onClick = { onFilterClick(filter) },
                        shape = RoundedCornerShape(percent = 100),
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .animateContentSize(),
                    ) {
                        if (filter.isSelected)
                            Icon(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp),
                                imageVector = Icons.Outlined.Done,
                                contentDescription = null
                            )
                        Text(stringResource(id = filter.name).caps, fontSize = 12.sp)
                    }
                }
            }
        }*/

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultItem(
    id: String,
    name: String,
    isSelected: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    isParentSelected: Boolean = false,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = { onLongClick(id) }, onClick = { onClick(id) })
    ) {
        val (parentSelectionStatusRef, selectionStatusRef, nameRef, isSelectedRef) = createRefs()

        if (isParentSelected)
            Icon(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .constrainAs(parentSelectionStatusRef) {
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(if (isSelected) selectionStatusRef.start else nameRef.start)
                    },
                imageVector = Icons.Outlined.ArrowCircleUp,
                contentDescription = stringResource(R.string.msg_inherit_selection),
            )

        AnimatedVisibility(
            isSelected,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .constrainAs(selectionStatusRef) {
                    centerVerticallyTo(parent)
                    start.linkTo(if (isParentSelected) parentSelectionStatusRef.end else parent.start)
                    end.linkTo(nameRef.start)
                }) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null
            )
        }

        Text(
            text = name,
            modifier = Modifier
                .padding(start = 4.dp)
                .constrainAs(nameRef) {
                    centerVerticallyTo(parent)
                    start.linkTo(if (isSelected) selectionStatusRef.end else if (isParentSelected) parentSelectionStatusRef.end else parent.start)
                    end.linkTo(isSelectedRef.start)
                    width = Dimension.fillToConstraints
                },
            fontSize = 16.sp
        )
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onCheckedChange(it) },
            modifier = Modifier
                .padding(end = 4.dp)
                .constrainAs(isSelectedRef) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end)
                },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary,
            )
        )
    }
}

data class Filter(
    val name: Int,
    val isSelected: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(name)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun Previ() {
//    SearchResultItem(
//        id = "Bafoussam",
//        name = "Bafoussam",
//        onCheckedChange = {},
//        onClick = {},
//        isSelected = true,
//        areAllChildrenSelected = true,
//        onLongClick = {}
//    )
//}