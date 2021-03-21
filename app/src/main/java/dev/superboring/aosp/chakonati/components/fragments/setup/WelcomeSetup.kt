package dev.superboring.aosp.chakonati.components.fragments.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.components.shared.CenteredColumn
import dev.superboring.aosp.chakonati.components.shared.HorizontallyCenteredBox
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.compose.stringRes

@Composable
fun WelcomeSetup() {
    Surface {
        CenteredColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            Title()
            AppDescription()
            StartNowButton()
            SkipStepLinkButton()
        }
    }
}

@Composable
private fun Title() {
    HorizontallyCenteredBox {
        Text(
            fontSize = 32.sp,
            text = String.format(
                R.string.setup_welcome_title.stringRes(),
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
            modifier = Modifier
                .padding(top = 24.dp, bottom = 56.dp),
            text = String.format(
                R.string.setup_welcome_app_desc.stringRes(),
                R.string.app_name.stringRes(),
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StartNowButton() {
    HorizontallyCenteredBox {
        Button(onClick = {}) {
            ResText(R.string.setup_welcome_setup_now)
        }
    }
}

@Composable
private fun SkipStepLinkButton() {
    HorizontallyCenteredBox(
        modifier = Modifier.padding(top = 12.dp)
    ) {
        TextButton(onClick = {}) {
            ResText(R.string.setup_welcome_skip_step)
        }
    }
}