package dev.superboring.aosp.chakonati.components.fragments.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import dev.superboring.aosp.chakonati.R
import dev.superboring.aosp.chakonati.components.shared.FullWidthColumn
import dev.superboring.aosp.chakonati.components.shared.IconButton
import dev.superboring.aosp.chakonati.components.shared.ResText
import dev.superboring.aosp.chakonati.components.shared.RoundedTextField
import dev.superboring.aosp.chakonati.components.shared.base.BareSurface

val textFieldHeight = 48.dp

@Composable
fun ComposeMessage(
    onSend: (message: String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    BareSurface(addPadding = false, modifier = Modifier.fillMaxWidth()) {
        FullWidthColumn {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .heightIn(textFieldHeight, textFieldHeight * 3),
            ) {
                val (textField, sendButton) = createRefs()
                RoundedTextField(
                    modifier = Modifier
                        .constrainAs(textField) {
                            start.linkTo(parent.start)
                            end.linkTo(sendButton.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .wrapContentWidth(Alignment.Start),
                    value = text,
                    placeholder = { ResText(R.string.chat__compose__compose_message) },
                    onValueChange = { text = it },
                )
                IconButton(
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    ),
                    onClick = { onSend(text) },
                    icon = Icons.Default.Send,
                    iconContentDescription = "",
                    modifier = Modifier
                        .size(textFieldHeight)
                        .requiredWidth(textFieldHeight)
                        .constrainAs(sendButton) {
                            start.linkTo(textField.end)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }
    }
}