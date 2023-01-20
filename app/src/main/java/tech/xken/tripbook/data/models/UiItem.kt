package tech.xken.tripbook.data.models

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * General ui metadata
 */
data class UiItem(
    val itemID: String = "",
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
)

class MasterUI {
    private val _items: MutableStateFlow<HashMap<String, UiItem>> = MutableStateFlow(hashMapOf(
        ROOT_ID to UiItem()
    ))
    val items = _items.asStateFlow()
    val root get() = _items.value[ROOT_ID]!!

    /**
     * Adds or update(in case of existence) a [UiItem] to the [MasterUI]
     */
    fun update(uiItem: UiItem) = apply {
        _items.value[uiItem.itemID] = uiItem
    }

    /**
     * Adds many update(in case of existence) [UiItem]s to the [MasterUI]
     */
    fun update(uiItems: List<UiItem>) = apply {
        uiItems.forEach {
            _items.value[it.itemID] = it
        }
    }

    /**
     * Removes a [UiItem] from the [MasterUI]
     */
    fun clear(itemID: String) = apply { _items.value.remove(itemID) }

    /**
     * Removes  [UiItem]s from the [MasterUI]
     */
    fun clear(itemsID: List<String>) = apply { itemsID.forEach { _items.value.remove(it) } }


    companion object {
        const val ROOT_ID = "screen"

    }
}