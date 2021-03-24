package dev.superboring.aosp.chakonati.components.fragments.setup

import android.content.ClipData
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.activities.ui.theme.additionalColors
import dev.superboring.aosp.chakonati.components.shared.*
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface
import dev.superboring.aosp.chakonati.compose.currentContext
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.persistence.dao.get
import dev.superboring.aosp.chakonati.persistence.dao.save
import dev.superboring.aosp.chakonati.persistence.dao.saveRelayServer
import dev.superboring.aosp.chakonati.persistence.db
import dev.superboring.aosp.chakonati.service.Communicator
import dev.superboring.aosp.chakonati.service.ownRelayCommunicator
import dev.superboring.aosp.chakonati.service.prepareOwnRelayCommunicator
import dev.superboring.aosp.chakonati.services.Setup
import dev.superboring.aosp.chakonati.signal.generateAndPublishPreKeys
import dev.superboring.aosp.chakonati.x.clipboard.clipboard
import dev.superboring.aosp.chakonati.x.handler.postMain
import dev.superboring.aosp.chakonati.x.toast.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RelayServerSetup(
    onPrevClick: () -> Unit, onNextClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var relayServer by remember { mutableStateOf("") }
    var failedRelayServer by remember { mutableStateOf("") }
    var verificationFailed by remember { mutableStateOf(false) }
    var verificationSucceeded by remember { mutableStateOf(false) }
    var failureReason by remember { mutableStateOf(null as String?) }
    val isRelayServerValid = relayServer.isNotEmpty()

    var hasCheckedPasswordSet by remember { mutableStateOf(false) }
    var isPasswordAlreadySet by remember { mutableStateOf(false) }
    var setupPassword by remember { mutableStateOf("") }

    var isSubmittingPassword by remember { mutableStateOf(false) }
    var hasCheckedSubmittedPassword by remember { mutableStateOf(false) }
    var isEnteredPasswordValid by remember { mutableStateOf(false) }

    var isSubmittingPreKeys by remember { mutableStateOf(false) }

    val relayServerInputPage = verificationFailed || !verificationSucceeded && !isVerifying
    val verificationPage = isVerifying
    val setupPasswordPage = hasCheckedPasswordSet && !isSubmittingPreKeys
    val submittingPreKeysPage = isEnteredPasswordValid && isSubmittingPreKeys
    val isSubmittingAnything = isVerifying || isSubmittingPassword || isSubmittingPreKeys
    val nextButtonDisabled = !isRelayServerValid || isSubmittingAnything

    val nextButtonClick: () -> Unit = {
        verificationFailed = false
        failureReason = null
        failedRelayServer = ""
        when {
            relayServerInputPage -> {
                isVerifying = true
                coroutineScope.launchIO {
                    ownRelayCommunicator = Communicator(relayServer)
                    val result = verifyRelayServer { password, isSet ->
                        setupPassword = password
                        isPasswordAlreadySet = isSet
                        hasCheckedPasswordSet = true
                    }
                    failureReason = result.second
                    failedRelayServer = relayServer
                    verificationFailed = !result.first
                    verificationSucceeded = result.first
                    isVerifying = false
                }
            }
            setupPasswordPage && isPasswordAlreadySet -> {
                isSubmittingPassword = true
                coroutineScope.launchIO {
                    isEnteredPasswordValid = Setup.isPasswordValid(setupPassword)
                    hasCheckedSubmittedPassword = true
                    isSubmittingPassword = false
                    if (isEnteredPasswordValid) {
                        db.mySetup().run {
                            saveRelayServer(relayServer)
                            get().apply {
                                relayServerPassword = setupPassword
                            }.save()
                        }

                        // Now submit the pre-keys
                        isSubmittingPreKeys = true
                        generateAndPublishPreKeys()

                        postMain(onNextClick)
                    }

                }
            }

            else -> onNextClick()
        }
    }
    val buttonBar = @Composable {
        ButtonBar(
            startButtonText = { ResText(R.string.common__back) },
            endButtonText = { ResText(R.string.common__next) },
            onStartButtonClick = onPrevClick,
            onEndButtonClick = nextButtonClick,
            hideStartButton = true,
            endButtonDisabled = nextButtonDisabled,
        )
    }

    Surface(color = additionalColors().actionBarWindowBackground) {
        BareSurface(addPadding = false) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { ResText(R.string.setup_relay_server__title) }
                    )
                },
                bottomBar = buttonBar
            ) {
                BareSurface(fill = true) {
                    when {
                        relayServerInputPage ->
                            EditForm(
                                relayServer = relayServer,
                                onRelayServerChange = { relayServer = it },
                                verificationFailed = verificationFailed,
                                failureReason = failureReason,
                                failedRelayServer = failedRelayServer,
                            )
                        verificationPage ->
                            Verification(
                                relayServer = relayServer
                            )
                        setupPasswordPage ->
                            PasswordSetup(
                                setupPassword, isPasswordAlreadySet, isSubmittingPassword,
                                hasCheckedSubmittedPassword, isEnteredPasswordValid,
                            ) {
                                setupPassword = it
                            }
                        submittingPreKeysPage ->
                            SubmittingPreKeys()
                    }
                }
            }
        }
    }
}

@Composable
private fun EditForm(
    onRelayServerChange: (server: String) -> Unit,
    relayServer: String,
    verificationFailed: Boolean,
    failureReason: String? = null,
    failedRelayServer: String = "",
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            ResText(R.string.setup_relay_server__enter_address_desc)

            Spacer(Modifier.height(8.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = relayServer,
                onValueChange = { onRelayServerChange(it) },
                label = { ResText(R.string.common__relay_server_address) },
                placeholder = { Text("chat.example.tld") },
                isError = verificationFailed,
            )
            if (verificationFailed) {
                Spacer(Modifier.height(1.dp))
                TextFieldErrorText(
                    String.format(
                        R.string.setup_relay_server__connection_failed.stringRes(),
                        failedRelayServer,
                        failureReason!!
                    ),
                )
            }
        }
    }
}

@Composable
private fun Verification(
    relayServer: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        Text(
            textAlign = TextAlign.Center,
            text = String.format(
                R.string.setup_relay_server__connecting_to_relay_server.stringRes(),
                relayServer
            )
        )
    }
}

private suspend fun verifyRelayServer(
    onPassword: (String, Boolean) -> Unit
): Pair<Boolean, String?> {
    return try {
        ownRelayCommunicator.doHandshake()
        val isPasswordSet = Setup.isPasswordSet()
        onPassword(if (!isPasswordSet) Setup.setPassword() else "", isPasswordSet)
        Pair(true, null)
    } catch (e: Exception) {
        Pair(false, e.localizedMessage)
    }
}

@Composable
private fun PasswordSetup(
    password: String, isPasswordAlreadySet: Boolean,
    isCheckingPassword: Boolean, hasCheckedPassword: Boolean, isPasswordValid: Boolean,
    onExistingPasswordInput: (password: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when {
            isCheckingPassword -> CheckingPassword()
            isPasswordAlreadySet -> EnterSetupPassword(
                hasCheckedPassword, isPasswordValid,
                password, onExistingPasswordInput
            )
            else -> NewSetupPassword(password)
        }
    }
}

@Composable
private fun NewSetupPassword(password: String) {
    ResText(R.string.setup_relay_server__password_desc_new)
    Spacer(Modifier.height(16.dp))
    HorizontallyCenteredBox {
        currentContext {
            Text(
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                text = password,
                textAlign = TextAlign.Center,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "setup password", password
                                )
                            )
                            showToast(R.string.setup_relay_server__setup_pw_copied_to_clipboard)
                        }
                    )

                }
            )
        }
    }
    Spacer(Modifier.height(16.dp))
    ResText(
        stringRes = R.string.setup_relay_server__tap_on_pw_to_copy,
        style = typography.caption,
    )
    Spacer(Modifier.height(16.dp))
    ResText(R.string.setup_relay_server__keep_password_safe)
}

@Composable
private fun EnterSetupPassword(
    hasCheckedPassword: Boolean,
    isPasswordValid: Boolean,
    setupPassword: String,
    onChange: (password: String) -> Unit
) {
    val showError = hasCheckedPassword && !isPasswordValid

    ResText(R.string.setup_relay_server__password_desc_pw_exists)
    Spacer(Modifier.height(16.dp))
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = setupPassword,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        label = { ResText(R.string.common__password) },
        placeholder = { ResText(R.string.setup_relay_server__password_placeholder) },
        onValueChange = onChange,
        isError = showError,
    )
    if (showError) {
        TextFieldErrorText(text = R.string.setup_relay_server__invalid_password.stringRes())
    }
}

@Composable
private fun CheckingPassword() {
    HorizontallyCenteredColumn {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        ResText(
            textAlign = TextAlign.Center,
            stringRes = R.string.setup_relay_server__checking_password,
        )
    }
}

@Composable
private fun SubmittingPreKeys() {
    HorizontallyCenteredColumn {
        Spacer(Modifier.height(16.dp))
        CircularProgressIndicator()
        Spacer(Modifier.height(8.dp))
        ResText(
            textAlign = TextAlign.Center,
            stringRes = R.string.setup_relay_server__submitting_pre_keys,
        )
    }
}