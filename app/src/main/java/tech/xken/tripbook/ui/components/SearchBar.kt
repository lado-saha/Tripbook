package tech.xken.tripbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.xken.tripbook.R
import tech.xken.tripbook.domain.caps


/**
 * @param onFieldSelected We need to specifically move the new field to the 0th position in the list
 */
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    fieldsStrRes: List<Int>,
    onFieldSelected: (newFieldIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    placeHolderResId: Int = R.string.lb_search,
    fieldsLazyListState: LazyListState = rememberLazyListState(),
    onBackClick: () -> Unit,
    onClearTextClick: () -> Unit,
    isError: () -> Boolean = { false },
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = if (searchText.isBlank()) RoundedCornerShape(50f) else RoundedCornerShape(100f),
        elevation = if (searchText.isBlank()) 1.dp else 5.dp,
        border = if (searchText.isBlank()) null else BorderStroke(
            width = 1.dp,
            color = if (isError()) MaterialTheme.colors.error else MaterialTheme.colors.primary
        )
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                value = searchText,
                placeholder = {
                    Text(
                        text = stringResource(placeHolderResId).caps,
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                onValueChange = {
                    onSearchTextChange(it)
                },
                trailingIcon = {
                    if (searchText.isNotBlank())
                        IconButton(onClick = { onClearTextClick() }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null
                            )
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
                            Text(stringResource(id = fieldsStrRes.first()).caps, fontSize = 12.sp)
                        }
                    }
                },
                singleLine = true
            )

            //We only show this when the search text is empty
            AnimatedVisibility(searchText.isBlank()) {
                LazyRow(
                    state = fieldsLazyListState,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    itemsIndexed(
                        items = fieldsStrRes.subList(1, fieldsStrRes.size)
                    ) { index, field ->
                        Button(
                            //Index + 1 Since we are excluding the already selected field which is at first index
                            onClick = { onFieldSelected(index + 1) },
                            shape = RoundedCornerShape(percent = 100),
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .animateContentSize(),
                        ) {
                            Text(text = stringResource(id = field).caps,
                                style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
        }

    }
}