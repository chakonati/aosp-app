package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.components.shared.AlertDialog
import dev.superboring.aosp.chakonati.components.shared.CenteredColumn
import dev.superboring.aosp.chakonati.components.shared.HorizontallyCenteredBox
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.base.StyledSurface
import dev.superboring.aosp.chakonati.compose.stringRes
import dev.superboring.aosp.chakonati.extensions.kotlinx.coroutines.launchIO
import dev.superboring.aosp.chakonati.signal.PreKeyBundle

@Composable
fun WelcomeSetup(
    onNext: () -> Unit,
    finishSetup: () -> Unit,
) {
    var shouldShowConfirmDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    StyledSurface {
        CenteredColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            Title()
            Spacer(Modifier.height(32.dp))
            AppDescription()
            Spacer(Modifier.height(4.dp))
            NextStepDescription()
            Spacer(Modifier.height(56.dp))
            StartNowButton(onNext)
            Spacer(Modifier.height(8.dp))
            SkipStepLinkButton { shouldShowConfirmDialog = true }
        }

        if (shouldShowConfirmDialog) {
            SkipStepConfirmDialog(
                onDismiss = { shouldShowConfirmDialog = false },
                onConfirm = {
                    coroutineScope.launchIO {
                        PreKeyBundle.generatePreKeys()
                        finishSetup()
                    }
                }
            )
        }
    }

}

@Composable
private fun Title() {
    HorizontallyCenteredBox {
        Text(
            fontSize = 32.sp,
            text = String.format(
                R.string.setup_welcome__title.stringRes(),
                R.string.app_name.stringRes(),
            ),
            textAlign = TextAlign.Center,
            style = typography.h1
        )
    }
}

@Composable
private fun AppDescription() {
    HorizontallyCenteredBox {
        Text(
            text = String.format(
                R.string.setup_welcome__app_desc.stringRes(),
                R.string.app_name.stringRes(),
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun NextStepDescription() {
    HorizontallyCenteredBox {
        ResText(
            stringRes = R.string.setup_welcome__reg_info,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StartNowButton(onClick: () -> Unit) {
    HorizontallyCenteredBox {
        Button(onClick = onClick) {
            ResText(R.string.setup_welcome__setup_now)
        }
    }
}

@Composable
private fun SkipStepLinkButton(onClick: () -> Unit) {
    HorizontallyCenteredBox {
        TextButton(onClick) {
            ResText(R.string.setup_welcome__skip_step)
        }
    }
}

@Composable
private fun SkipStepConfirmDialog(
    enabled: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { ResText(R.string.setup_welcome__skip_step_dialog__title) },
        text = { ResText(R.string.setup_welcome__skip_step_dialog__text) },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = enabled) {
                ResText(R.string.setup_welcome__skip_step_dialog__no)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = enabled) {
                ResText(R.string.setup_welcome__skip_step_dialog__yes)
            }
        },
    )
}