package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.IconButton
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.RoundedTextField
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface

@Composable
fun ComposeMessage() {
    BareSurface {
        FullWidthColumn {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                RoundedTextField(
                    modifier = Modifier.wrapContentWidth(),
                    value = "",
                    placeholder = { ResText(R.string.chat__compose__compose_message) },
                    onValueChange = { },
                )
                IconButton(
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    ),
                    onClick = { },
                    icon = Icons.Default.Send,
                    iconContentDescription = ""
                )
            }
        }
    }
}