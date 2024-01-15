package tech.xken.tripbook.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrowseGallery
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Undo
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import tech.xken.tripbook.BuildConfig
import tech.xken.tripbook.R
import tech.xken.tripbook.data.models.ActionItem
import tech.xken.tripbook.data.models.ActionSheet
import tech.xken.tripbook.data.models.MainAction
import tech.xken.tripbook.domain.caps
import tech.xken.tripbook.domain.createImageFile
import tech.xken.tripbook.domain.disableComposable
import tech.xken.tripbook.ui.components.InfoDialog
import tech.xken.tripbook.ui.components.InfoDialogUiState
import tech.xken.tripbook.ui.screens.ImageViewerDialogStatus.*
import java.util.Objects

//fun promptForPermission(permission: String) {
//    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//    val uri = Uri.fromParts("package", packageName, null)
//    intent.data = uri
//
//    startActivity(intent)
//}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ImageViewer(
    vm: ImageViewerVM = hiltViewModel(),
    onNavigateBack: (uiStateStr: String?) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val uis by vm.uiState.collectAsState()
    val context = LocalContext.current

    val image = remember {
        context.createImageFile()
    }

    val tempFileUri = remember {
        mutableStateOf(
            FileProvider.getUriForFile(
                Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", image
            )
        )
    }

    // Launches the camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            vm.onUriChange(tempFileUri.value)
        }
    }
    // Request permission to use camera before launching camera else shows user a dialog to retry/inform when there is denial
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) cameraLauncher.launch(tempFileUri.value) else {
            vm.onDialogStatusChange(
                CAMERA_PERMISSION_DENIED
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        context as Activity,
//                        Manifest.permission.CAMERA
//                    )
//                ) CAMERA_PERMISSION_DENIED else CAMERA_PERMISSION_PERMANENTLY_DENIED
            )
        }
    }
    // Launches intent for the gallery
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val persistentUri = it
//                context.contentResolver.takePersistableUriPermission(
//                    persistentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
                vm.onUriChange(persistentUri)
            }
        }

    // Request permission to use gallery before launching gallery else shows user a dialog to retry/inform when there is denial
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) galleryLauncher.launch(arrayOf("image/*"))
        else {
            vm.onDialogStatusChange(
                GALLERY_PERMISSION_DENIED
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        context as Activity,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    )
//                ) GALLERY_PERMISSION_DENIED else GALLERY_PERMISSION_PERMANENTLY_DENIED
            )
        }
    }

    if (uis.isComplete) {
        vm.onCompleteChange(false)
        onNavigateBack(vm.navArgs)
    }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            when (it) {
                ModalBottomSheetValue.Hidden -> {
                    vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                }

                ModalBottomSheetValue.Expanded -> {}
                ModalBottomSheetValue.HalfExpanded -> {}
            }
            true
        }
    )

    // Hides or shows the bottom sheet
    LaunchedEffect(uis.sheetStatus) {
        if (uis.sheetStatus != ImageViewerSheetStatus.NONE) {
            sheetState.show()
        } else if (sheetState.targetValue != ModalBottomSheetValue.Hidden || sheetState.currentValue != ModalBottomSheetValue.Hidden) {
            sheetState.hide()
        }
    }

    fun handleBackNav() =
        if (uis.imageUiState.localUri != vm.oldUri)
            vm.onDialogStatusChange(LEAVING_WITHOUT_SAVING)
        else if (uis.imageUiState.run { localUri == Uri.EMPTY && url == "" })
            vm.onDialogStatusChange(LEAVING_WITH_NO_PICTURE)
        else {
            onNavigateBack(null)
        }

    BackHandler(true) {
        handleBackNav()
    }

    // Manages showing the Info dialogs
    InfoDialogs(
        uis,
        vm,
        { onNavigateBack(null) },
        cameraPermissionLauncher,
        galleryPermissionLauncher
    )

    ModalBottomSheetLayout(
        sheetContent = {
            BottomSheetContent(
                uis,
                vm,
                context,
                galleryLauncher,
                galleryPermissionLauncher,
                cameraLauncher,
                tempFileUri,
                cameraPermissionLauncher
            )
        },
        sheetShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(10), topStart = CornerSize(10),
        ),
        sheetState = sheetState,
        sheetElevation = 1.dp,
        scrimColor = ModalBottomSheetDefaults.scrimColor.copy(alpha = 0.1f)
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(elevation = 0.dp,
                    title = {
                        Text(
                            text = uis.imageUiState.title,
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }, navigationIcon = {
                        IconButton(onClick = {
                            handleBackNav()
                        }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = LocalContentColor.current
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    actions = {
                        if (uis.imageUiState.canUndo && vm.oldUri != uis.imageUiState.localUri)
                            IconButton(
                                onClick = { vm.onUriChange(vm.oldUri) },
                            ) {
                                Icon(
                                    Icons.Outlined.Undo,
                                    contentDescription = stringResource(id = R.string.desc_undo_changes),
                                    tint = LocalContentColor.current
                                )
                            }

                        if (uis.imageUiState.isEditable)
                            IconButton(
                                onClick = { vm.onSheetStatusChange(ImageViewerSheetStatus.PICK_PICTURE) },
                                enabled = uis.imageUiState.isEditable
                            ) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = stringResource(id = R.string.desc_change_picture),
                                    tint = LocalContentColor.current
                                )
                            }

                        if (uis.imageUiState.canDelete)
                            IconButton(
                                onClick = { vm.onUriChange(Uri.EMPTY) }
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = stringResource(id = R.string.desc_delete_picture),
                                    tint = MaterialTheme.colors.error
                                )
                            }

                        if (uis.imageUiState.localUri != vm.oldUri)
                            IconButton(onClick = {
                                vm.onCompleteChange(true)
                            }) {
                                Icon(
                                    Icons.Outlined.Save,
                                    contentDescription = stringResource(id = R.string.desc_save_changes),
                                    tint = LocalContentColor.current
                                )
                            }

                        IconButton(onClick = {
                            vm.onSheetStatusChange(ImageViewerSheetStatus.ACTIONS)
                        }) {
                            Icon(
                                Icons.Outlined.MoreVert,
                                contentDescription = stringResource(id = R.string.desc_more_options),
                                tint = LocalContentColor.current
                            )
                        }
                    },
                    backgroundColor = Color.Unspecified
                )
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .disableComposable(uis.isLoading || !uis.isInitComplete)
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = uis.isLoading, modifier = Modifier.padding(4.dp)
                ) {
                    CircularProgressIndicator()
                }

                if (uis.isInitComplete) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).also {
                            it.data(uis.imageUiState.run { localUri })
                            it.memoryCachePolicy(CachePolicy.DISABLED)
                            it.diskCachePolicy(CachePolicy.DISABLED)
                            it.crossfade(true)
                        }.build(),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .graphicsLayer(
                                scaleX = uis.scale,
                                scaleY = uis.scale,
                                translationX = if (uis.scale > 1f) uis.offset.x else 0f,
                                translationY = if (uis.scale > 1f) uis.offset.y else 0f
                            )
                            .pointerInput("1") {
                                if (!uis.imageUiState.isFailure)
                                    detectTapGestures(
                                        onDoubleTap = { pan ->
                                            if (uis.scale <= 1f) vm.onScaleChange(2f) else vm.onScaleChange(
                                                0.9f
                                            )
                                        }
                                    )
                            }
                            .pointerInput("2") {
                                if (!uis.imageUiState.isFailure)
                                    detectTransformGestures(
                                        onGesture = { _, pan: Offset, zoom: Float, _ ->
                                            vm.onOffsetChange(uis.offset + pan)
                                            vm.onScaleChange((uis.scale * zoom).coerceIn(0.9f, 4f))
                                        }
                                    )
                            },
                        contentScale = ContentScale.Fit,
                        onLoading = {
                            vm.onFailureChange(false)
                            vm.onLoadingChange(true)
                        },
                        onSuccess = {
                            vm.onLoadingChange(false)
                            vm.onFailureChange(false)
                        },
                        onError = {
                            vm.onFailureChange(true)
                            vm.onLoadingChange(false)
                        },
                        placeholder = rememberVectorPainter(image = uis.imageUiState.placeholder),
                        error = rememberVectorPainter(image = uis.imageUiState.placeholder)
                    )
                }

            }
        }
    }

    SnackbarManager(uis = uis, scaffoldState = scaffoldState, vm = vm)
}

@Composable
private fun BottomSheetContent(
    uis: ImageViewerUiState,
    vm: ImageViewerVM,
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    galleryPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    tempFileUri: MutableState<Uri>,
    cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    when (uis.sheetStatus) {
        ImageViewerSheetStatus.NONE -> {}
        ImageViewerSheetStatus.ACTIONS -> {
            ActionSheet(
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionItem(
                    action = MainAction(R.string.lb_about_picture, Icons.Outlined.Info),
                    onClick = {
                        vm.onDialogStatusChange(ABOUT_PICTURE)
                        vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                    },
                )

            }
        }

        ImageViewerSheetStatus.PICK_PICTURE -> {
            ActionSheet(
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionItem(
                    action = MainAction(
                        R.string.lb_browse_gallery,
                        Icons.Outlined.PhotoLibrary
                    ),
                    onClick = {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(/* context = */ context, /* permission = */
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )

                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                            galleryLauncher.launch(arrayOf("image/*"))
                        } else {
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                                    context as Activity,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE
//                                )
//                            )
                                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//                            else vm.onDialogStatusChange(
//                                GALLERY_PERMISSION_PERMANENTLY_DENIED
//                            )
                            // Request a permission
                        }
                        vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                    },
                )

                ActionItem(
                    action = MainAction(
                        R.string.lb_from_camera,
                        Icons.Outlined.CameraAlt
                    ),
                    onClick = {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(/* context = */ context, /* permission = */
                                Manifest.permission.CAMERA
                            )
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(tempFileUri.value)
                        } else {

//                            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                                    context as Activity,
//                                    Manifest.permission.CAMERA
//                                )
//                            ) {
                                vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                            } else
//                                vm.onDialogStatusChange(
//                                    CAMERA_PERMISSION_PERMANENTLY_DENIED
//                                )

                            // Request a permission
                        }
                        vm.onSheetStatusChange(ImageViewerSheetStatus.NONE)
                    },
                )

            }
        }
    }
}

@Composable
private fun InfoDialogs(
    uis: ImageViewerUiState,
    vm: ImageViewerVM,
    navigateAwayWithoutSaving: () -> Unit,
    cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    galleryPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    val statusMap = remember {
        mapOf(
            CAMERA_PERMISSION_DENIED to InfoDialogUiState(
                mainIcon = Icons.Outlined.CameraAlt,
                title = "Permission Denied to take picture",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ),
            GALLERY_PERMISSION_DENIED to InfoDialogUiState(
                mainIcon = Icons.Outlined.BrowseGallery,
                title = "Permission Denied to browse gallery",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true,
            ),
            CAMERA_PERMISSION_PERMANENTLY_DENIED to InfoDialogUiState(
                mainIcon = Icons.Outlined.CameraAlt,
                title = "Permission Permanently denied to take picture",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand",
            ),
            GALLERY_PERMISSION_PERMANENTLY_DENIED to InfoDialogUiState(
                mainIcon = Icons.Outlined.BrowseGallery,
                title = "Permission Permanently denied to browse picture",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand",
            ),// To be provided by the caller
            ABOUT_PICTURE to InfoDialogUiState(
                mainIcon = Icons.Outlined.PersonPin,
                title = "Account Photo",
                text = buildAnnotatedString {
                    append("Text") //TODO: Add profile page hel text
                },
                positiveText = "I understand"
            ),
            LEAVING_WITHOUT_SAVING to InfoDialogUiState(
                mainIcon = Icons.Outlined.Save,
                title = "Some changes may be lost",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Save changes",
                otherText = "Discard",
                isNegative = true
            ),
            COULD_NOT_GET_PICTURE to InfoDialogUiState(
                mainIcon = Icons.Filled.PersonOff,
                title = "Could not load photo",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Retry",
                otherText = "Cancel",
                isNegative = true
            ),
            LEAVING_WITH_NO_PICTURE to InfoDialogUiState(
                mainIcon = Icons.Filled.ErrorOutline,
                title = "Leaving with empty account",
                text = buildAnnotatedString {
                    append("Text here")
                },
                positiveText = "Stay",
                otherText = "Leave",
                isNegative = true
            )
        )
    }
    when (val status = uis.dialogStatus) {
        NONE -> {}

        ABOUT_PICTURE -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            })
        }

        LEAVING_WITHOUT_SAVING -> {
            InfoDialog(uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                },
                onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                    vm.onCompleteChange(true)
                },
                onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                    navigateAwayWithoutSaving()
                }
            )
        }

        LEAVING_WITH_NO_PICTURE -> {
            InfoDialog(uis = statusMap[status]!!,
                onCloseClick = {
                    vm.onDialogStatusChange(NONE)
                },
                onPositiveClick = {
                    vm.onDialogStatusChange(NONE)
                },
                onOtherClick = {
                    vm.onDialogStatusChange(NONE)
                    navigateAwayWithoutSaving()
                })
        }

        COULD_NOT_GET_PICTURE -> {
            InfoDialog(uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStatusChange(NONE)
            }, onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            }, onOtherClick = {
                vm.onDialogStatusChange(NONE)
            })
        }

        CAMERA_PERMISSION_DENIED -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                vm.onDialogStatusChange(NONE)
            },
            onOtherClick = {
                vm.onDialogStatusChange(NONE)
            },
        )

        GALLERY_PERMISSION_DENIED -> InfoDialog(
            uis = statusMap[status]!!, onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                vm.onDialogStatusChange(NONE)
            }, onOtherClick = {
                vm.onDialogStatusChange(NONE)
            }
        )

        CAMERA_PERMISSION_PERMANENTLY_DENIED -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            },
            mainIconTint = MaterialTheme.colors.error
        )

        GALLERY_PERMISSION_PERMANENTLY_DENIED -> InfoDialog(
            uis = statusMap[status]!!,
            onCloseClick = {
                vm.onDialogStatusChange(NONE)
            },
            onPositiveClick = {
                vm.onDialogStatusChange(NONE)
            },
            mainIconTint = MaterialTheme.colors.error
        )
    }
}

/**
 * Shows the snackbar messages
 */
@Composable
private fun SnackbarManager(
    uis: ImageViewerUiState,
    scaffoldState: ScaffoldState,
    vm: ImageViewerVM
) {
    if (uis.message != null) {
        val message = stringResource(id = uis.message).caps
        LaunchedEffect(scaffoldState, vm, uis.message) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message, /*actionLabel = retryActionLabel*/
            ).also {
                if (it == SnackbarResult.ActionPerformed) when (uis.message) {

                }
            }
            vm.onMessageChange(null)
        }
    }
}


